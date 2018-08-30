/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics Measurement
 * protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert, Daniel Murphy
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package uk.ac.sussex.gdsc.analytics;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common tracking calls are implemented as methods, but if you want to control
 * what data to send, then use {@link #makeCustomRequest(RequestParameters)}. If
 * you are making custom calls, the only requirements are:
 * <ul>
 * <li>{@link RequestParameters#setDocumentPath(String)} must be populated</li>
 * </ul>
 * See the <a
 * href=http://code.google.com/intl/en-US/apis/analytics/docs/tracking/gaTrackingTroubleshooting.html#gifParameters>
 * Google Troubleshooting Guide</a> for more info on the tracking parameters
 * (although it doesn't seem to be fully updated).
 * <p>
 * The tracker can operate in three modes:
 * </p>
 * <ul>
 * <li>synchronous mode: The HTTP request is sent to GA immediately, before the
 * track method returns. This may slow your application down if GA doesn't
 * respond fast.
 * <li>multi-thread mode: Each track method call creates a new short-lived
 * thread that sends the HTTP request to GA in the background and terminates.
 * <li>single-thread mode (the default): The track method stores the request in
 * a FIFO and returns immediately. A single long-lived background thread
 * consumes the FIFO content and sends the HTTP requests to GA.
 * </ul>
 * <p>
 * To halt the background thread safely, use the call
 * {@link #stopBackgroundThread(long)}, where the parameter is the timeout to
 * wait for any remaining queued tracking calls to be made. Keep in mind that if
 * new tracking requests are made after the thread is stopped, they will just be
 * stored in the queue, and will not be sent to GA until the thread is started
 * again with {@link #setDispatchMode(DispatchMode)} using
 * {@link DispatchMode#SINGLE_THREAD}.
 * </p>
 * <p>
 * Note: This class has been copied from the JGoogleAnalyticsTracker project and
 * modified by Alex Herbert to:
 * </p>
 * <ul>
 * <li>Alter the data sent to Google Analytics to use the POST method for the
 * Measurement Protocol
 * <li>Remove the slfj dependency
 * <li>Make the logger specific to the instance (since each instance may have a
 * different GA tracking ID)
 * </ul>
 * <p>
 * The architecture for dispatching messages is unchanged.
 * </p>
 *
 * @author Daniel Murphy, Stefan Brozinski, Alex Herbert
 */
public class JGoogleAnalyticsTracker {
    /**
     * The dispatch mode
     */
    public static enum DispatchMode {
    /**
     * Each tracking call will wait until the http request completes before
     * returning
     */
    SYNCHRONOUS,
    /**
     * Each tracking call spawns a new thread to make the http request
     */
    MULTI_THREAD,
    /**
     * Each tracking request is added to a queue, and a single dispatch thread makes
     * the requests.
     * <p>
     * The dispatch thread is shared by all instances of the class. To avoid your
     * tracking calls being swamped by another tracker you could use the
     * {@link #MULTI_THREAD} option and let the JVM figure out which request
     * dispatch thread to run.
     */
    SINGLE_THREAD
    }

    /**
     * Allow the correct instance to be put into the request queue. This means that
     * if the logger on the instance is changed after the request is added to the
     * queue then the correct logger can be obtained from the instance getLogger()
     * method.
     */
    private static class RequestData {
        final RequestParameters requestParameters;
        final long timestamp;
        final JGoogleAnalyticsTracker tracker;

        RequestData(RequestParameters requestParameters, long timestamp, JGoogleAnalyticsTracker tracker) {
            this.requestParameters = requestParameters;
            this.timestamp = timestamp;
            this.tracker = tracker;
        }
    }

    private static final ThreadGroup asyncThreadGroup = new ThreadGroup("Async Google Analytics Threads");
    private static AtomicLong asyncThreadsRunning = new AtomicLong(0);
    private static Proxy proxy = Proxy.NO_PROXY;
    // private static Queue<RequestData> fifo = new LinkedList<RequestData>();
    private static BlockingQueue<RequestData> fifo = new LinkedBlockingQueue<>();
    private static Thread backgroundThread = null; // the thread used in 'queued' mode.
    private static boolean backgroundThreadMayRun = false;
    // Java 1.6
    private static Charset cs = Charset.forName("UTF-8");
    // Java 1.7. Not yet used by this library.
    // private static Charset cs = StandardCharsets.UTF_8;

    static {
        asyncThreadGroup.setMaxPriority(Thread.MIN_PRIORITY);
        asyncThreadGroup.setDaemon(true);
    }

    /**
     * The Protocol version. This will only change when there are changes made that
     * are not backwards compatible.
     *
     * @author a.herbert@sussex.ac.uk
     */
    public static enum MeasurementProtocolVersion {
        /**
         * Version 1
         */
        V_1
    }

    private Logger logger = Logger.getLogger(JGoogleAnalyticsTracker.class.getName());
    private final ClientParameters clientParameters;
    private final IAnalyticsMeasurementProtocolURLBuilder builder;
    private DispatchMode mode;
    private boolean enabled;
    private boolean secure;

    /**
     * Create an instance
     *
     * @param clientParameters The client parameters
     * @param version          The GA version
     */
    public JGoogleAnalyticsTracker(ClientParameters clientParameters, MeasurementProtocolVersion version) {
        this(clientParameters, version, DispatchMode.SINGLE_THREAD);
    }

    /**
     * Create an instance
     *
     * @param clientParameters The client parameters
     * @param version          The GA version
     * @param dispatchMode     The dispatch mode
     */
    public JGoogleAnalyticsTracker(ClientParameters clientParameters, MeasurementProtocolVersion version,
            DispatchMode dispatchMode) {
        this.clientParameters = Objects.requireNonNull(clientParameters, "Client parameters is null");
        builder = createBuilder(version);
        enabled = true;
        setDispatchMode(dispatchMode);
    }

    /**
     * Sets the dispatch mode
     *
     * @see DispatchMode
     * @param mode the mode to to put the tracker in. If this is null, the tracker
     *             defaults to {@link DispatchMode#SINGLE_THREAD}
     */
    public void setDispatchMode(DispatchMode mode) {
        if (mode == null)
            mode = DispatchMode.SINGLE_THREAD;
        if (mode == DispatchMode.SINGLE_THREAD)
            startBackgroundThread(logger);
        this.mode = mode;
    }

    /**
     * Gets the current dispatch mode. Default is
     * {@link DispatchMode#SINGLE_THREAD}.
     *
     * @return the dispatch mode
     * @see DispatchMode
     */
    public DispatchMode getDispatchMode() {
        return mode;
    }

    /**
     * Convenience method to check if the tracker is in synchronous mode.
     *
     * @return true, if is synchronous
     */
    public boolean isSynchronous() {
        return mode == DispatchMode.SYNCHRONOUS;
    }

    /**
     * Convenience method to check if the tracker is in single-thread mode.
     *
     * @return true, if is single threaded
     */
    public boolean isSingleThreaded() {
        return mode == DispatchMode.SINGLE_THREAD;
    }

    /**
     * Convenience method to check if the tracker is in multi-thread mode.
     *
     * @return true, if is multi threaded
     */
    public boolean isMultiThreaded() {
        return mode == DispatchMode.MULTI_THREAD;
    }

    /**
     * Sets if the api dispatches tracking requests.
     *
     * @param enabled the new enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * If the api is dispatching tracking requests (default of true).
     *
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Checks if is using HTTPS.
     *
     * @return true, if is secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets to true to use HTTPS.
     *
     * @param secure the new secure
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Define the proxy to use for all GA tracking requests. You can pass
     * Proxy.NO_PROXY to explicit use no proxy. Pass null to revert to the system
     * default mechanism for connecting.
     * <p>
     * Call this static method early (before creating any tracking requests).
     *
     * @param proxy The proxy to use
     */
    public static void setProxy(Proxy proxy) {
        if (proxy == null)
            proxy = Proxy.NO_PROXY;
        JGoogleAnalyticsTracker.proxy = proxy;
    }

    /**
     * Define the proxy to use for all GA tracking requests.
     * <p>
     * Call this static method early (before creating any tracking requests).
     * <p>
     * If no proxy can be set then this will reset the proxy to
     * {@link Proxy#NO_PROXY}.
     *
     * @param proxyAddress "hostname:port" of the proxy to use; may also be given as
     *                     URL ("http://hostname:port/").
     * @return true, if successful
     */
    public static boolean setProxy(String proxyAddress) {
        if (proxyAddress != null) {
            // Split into "hostname:port"
            final Matcher m = Pattern.compile("^(https?://|)([^ :]+):([0-9]+)").matcher(proxyAddress);
            if (m.find()) {
                final String hostname = m.group(2);
                final int port = Integer.parseInt(m.group(3));

                final SocketAddress sa = new InetSocketAddress(hostname, port);
                setProxy(new Proxy(Type.HTTP, sa));
                return true;
            }
        }
        setProxy(Proxy.NO_PROXY);
        return false;
    }

    /**
     * Wait for background tasks to complete.
     * <p>
     * This works in {@link DispatchMode#SINGLE_THREAD} and
     * {@link DispatchMode#MULTI_THREAD} mode.
     *
     * @param timeoutMillis The maximum number of milliseconds to wait.
     */
    public static void completeBackgroundTasks(long timeoutMillis) {
        boolean fifoEmpty = false;
        boolean asyncThreadsCompleted = false;

        final long absTimeout = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < absTimeout) {
            fifoEmpty = fifo.size() == 0;
            asyncThreadsCompleted = (asyncThreadsRunning.get() == 0);

            if (fifoEmpty && asyncThreadsCompleted)
                break;

            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Makes a custom tracking request based on the given data.
     *
     * @param requestParameters The request parameters
     * @throws NullPointerException if requestData is null
     */
    public void makeCustomRequest(RequestParameters requestParameters) {
        makeCustomRequest(requestParameters, System.currentTimeMillis());
    }

    /**
     * Makes a custom tracking request based on the given data.
     *
     * @param requestParameters The request parameters
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     * @throws NullPointerException if requestData is null
     */
    public void makeCustomRequest(final RequestParameters requestParameters, final long timestamp) {
        if (!enabled) {
            logger.fine("Ignoring tracking request, enabled is false");
            return;
        }
        Objects.requireNonNull(requestParameters, "Request parameters cannot be null");

        switch (mode) {
        case MULTI_THREAD:
            final Thread t = new Thread(asyncThreadGroup, "AnalyticsThread-" + asyncThreadGroup.activeCount()) {
                @Override
                public void run() {
                    asyncThreadsRunning.getAndIncrement();
                    try {
                        dispatchRequest(builder, clientParameters, requestParameters, timestamp, logger, secure);
                    } finally {
                        asyncThreadsRunning.getAndDecrement();
                    }
                }
            };
            t.setDaemon(true);
            t.start();
            break;

        case SYNCHRONOUS:
            dispatchRequest(builder, clientParameters, requestParameters, timestamp, logger, secure);
            break;

        case SINGLE_THREAD:
        default: // in case it's null, we default to the single-thread
            fifo.add(new RequestData(requestParameters, timestamp, this));
            if (!backgroundThreadMayRun)
                logger.severe("A tracker request has been added to the queue but the background thread isn't running.");
            break;
        }
    }

    /**
     * Send the parameters to Google Analytics using the Measurement Protocol. This
     * uses the HTTP POST method.
     *
     * @param builder           The URL builder for Google Analytics Measurement
     *                          Protocol
     * @param clientParameters  The client parameter data
     * @param requestParameters The request parameter data
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     * @param logger            The logger used for status messages
     * @param secure            the secure
     */
    private static void dispatchRequest(IAnalyticsMeasurementProtocolURLBuilder builder,
            ClientParameters clientParameters, RequestParameters requestParameters, long timestamp, Logger logger,
            boolean secure) {
        HttpURLConnection connection = null;
        try {
            final String parameters = builder.buildURL(clientParameters, requestParameters, timestamp);
            final URL url = new URL(
                    (secure) ? "https://www.google-analytics.com/collect" : "http://www.google-analytics.com/collect");
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            // Java 1.5 method
            // final byte[] out = parameters.getBytes("UTF-8");
            // Java 1.6 method
            final byte[] out = parameters.getBytes(cs);
            final int length = out.length;
            connection.setFixedLengthStreamingMode(length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.connect();
            try (final OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                logger.severe(() -> String.format(
                        "JGoogleAnalyticsTracker: Error requesting url '%s', received response code %d", parameters,
                        responseCode));
            } else {
                logger.fine(() -> String.format("JGoogleAnalyticsTracker: Tracking success for url '%s'", parameters));
            }
        } catch (final Exception e) {
            logger.severe(() -> "Error making tracking request: " + e.getMessage());
            // Q. Should this rethrow?
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    /**
     * Creates the URL builder.
     *
     * @param version the version
     * @return the URL builder
     */
    private static IAnalyticsMeasurementProtocolURLBuilder createBuilder(MeasurementProtocolVersion version) {
        switch (version) {
        case V_1:
        default:
            return new AnalyticsMeasurementProtocolURLBuilder();
        }
    }

    /**
     * If the background thread for 'queued' mode is not running, start it now.
     */
    private synchronized static void startBackgroundThread(final Logger logger) {
        if (backgroundThread == null || !backgroundThread.isAlive()) {
            backgroundThreadMayRun = true;
            backgroundThread = new Thread(asyncThreadGroup, "AnalyticsBackgroundThread") {
                @Override
                public void run() {
                    logger.fine("AnalyticsBackgroundThread started");
                    try {
                        while (backgroundThreadMayRun) {
                            final RequestData data = fifo.take();
                            if (data.requestParameters == null) {
                                // Ignore shutdown signals
                                continue;
                            }
                            dispatchRequest(data.tracker.builder, data.tracker.clientParameters, data.requestParameters,
                                    data.timestamp, data.tracker.logger, data.tracker.secure);
                        }
                    } catch (final InterruptedException e) {
                        logger.warning(() -> "Background thread interrupted: " + e.getMessage());
                    } finally {
                        backgroundThreadMayRun = false;
                    }
                }
            };

            // Don't prevent the application from terminating.
            // Use completeBackgroundTasks() before exit if you want to ensure
            // that all pending GA requests are sent.
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        }
    }

    /**
     * Stop the long-lived background thread.
     * <p>
     * This method is needed for debugging purposes only. Calling it in an
     * application is not really required: The background thread will terminate
     * automatically when the application exits.
     *
     * @param timeoutMillis If nonzero, wait for thread completion before returning.
     */
    public synchronized static void stopBackgroundThread(long timeoutMillis) {
        backgroundThreadMayRun = false;
        // Pass a shutdown signal to the queue
        fifo.add(new RequestData(null, 0, null));
        if ((backgroundThread != null) && (timeoutMillis > 0)) {
            try {
                backgroundThread.join(timeoutMillis);
            } catch (final InterruptedException e) {
                // Ignore
            }
            backgroundThread = null;
        }
    }

    /**
     * Get the logger
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Set the logger
     *
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        // If null set to a turned off logger
        if (logger == null) {
            logger = Logger.getAnonymousLogger();
            logger.setLevel(Level.OFF);
        }
        this.logger = logger;
    }

    /**
     * Reset the session (i.e. start a new session)
     */
    public void resetSession() {
        clientParameters.resetSession();
    }

    /**
     * Set the state of IP anonymization
     *
     * @param anonymized True if the IP address of the sender will be anonymized
     */
    public void setAnonymised(boolean anonymized) {
        clientParameters.setAnonymized(anonymized);
    }
}
