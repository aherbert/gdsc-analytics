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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@formatter:off
/**
 * Send custom requests to Google Analytics (GA) using the <a href=
 * "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google
 * Analytics Measurement Protocol</a>.
 *
 * <p>The tracker can operate in three modes:
 *
 * <ul>
 * <li>Synchronous mode: The request is sent immediately.
 * <li>Multi-thread mode: The request is sent using a background thread,
 *     one for each request.
 * <li>Single-thread mode (the default): The request is queued and
 *     processed using a single-background thread.
 * </ul>
 *
 * <p>Note: The ideas for this class are based on the JGoogleAnalyticsTracker project.
 *
 * @see <a href=
 *      "https://code.google.com/archive/p/jgoogleanalyticstracker/">JGoogleAnalyticsTracker</a>
 */
//@formatter:on
public class GoogleAnalyticsTracker {

    /** The connection URL for Google Analytics over HTTP. */
    public static final String HTTP_GOOGLE_ANALYTICS_URL = "http://www.google-analytics.com/collect";
    /** The connection URL for Google Analytics over HTTPS. */
    public static final String HTTPS_GOOGLE_ANALYTICS_URL = "https://www.google-analytics.com/collect";

    /**
     * The dispatch mode
     */
    public static enum DispatchMode {
        /**
         * Each tracking call will wait until the http request completes before
         * returning
         */
        SYNCHRONOUS {
            @Override
            public boolean isAsynchronous() {
                return false;
            }
        },
        /**
         * Each tracking call spawns a new thread to make the http request
         */
        MULTI_THREAD,
        /**
         * Each tracking request is added to a queue, and a single dispatch thread makes
         * the requests.
         *
         * <p>The dispatch thread is shared by all instances of the class. To avoid your
         * tracking calls being swamped by another tracker you could use the
         * {@link #MULTI_THREAD} option and let the JVM figure out which request
         * dispatch thread to run.
         */
        SINGLE_THREAD;

        /**
         * Checks if the mode is asynchronous (i.e. in the background).
         *
         * @return true, if asynchronous
         */
        public boolean isAsynchronous() {
            return true;
        }
    }

    /**
     * The dispatch status
     */
    public static enum DispatchStatus {
        /**
         * The request has been ignored. This occurs when the tracked is disabled.
         */
        IGNORED,
        /**
         * The request has been processed. This occurs when using
         * {@link DispatchMode#SYNCHRONOUS}.
         */
        COMPLETE,
        /**
         * The request has errored. This occurs when using
         * {@link DispatchMode#SYNCHRONOUS}.
         */
        ERROR,
        /**
         * The request is running. This occurs when using
         * {@link DispatchMode#MULTI_THREAD} or {@link DispatchMode#SINGLE_THREAD}.
         */
        RUNNING;
    }

    /** The logger for all static methods. */
    private static final Logger logger = Logger.getLogger(GoogleAnalyticsTracker.class.getName());

    /** The executor service for {@link DispatchMode#MULTI_THREAD} mode. */
    private static final ExecutorService multiThreadExecutor;

    /** The executor service for {@link DispatchMode#SINGLE_THREAD} mode. */
    private static final ExecutorService singleThreadExecutor;

    /**
     * The count of the number of tasks waiting in the background.
     */
    private static AtomicLong backgroundTasks = new AtomicLong(0);

    /** The proxy using for tracking requests. */
    private static Proxy proxy = null;

    /**
     * The last IO exception that occurred when dispatching a request. If this is
     * not null then the tracker is disabled as it is assumed that all subsequent
     * tracking requests will fail.
     */
    private static volatile IOException lastIOException = null;

    /** The thread number, incremented for new threads. */
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    static {
        multiThreadExecutor = Executors.newCachedThreadPool((r) -> newThread(r));
        singleThreadExecutor = Executors.newFixedThreadPool(1, (r) -> newThread(r));
    }

    /**
     * Create a new background thread.
     *
     * @param r the runnable
     * @return the thread
     */
    private static Thread newThread(Runnable r) {
        final Thread t = new Thread(r, "GoogleAnalyticsThread-" + threadNumber.getAndIncrement());
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    }

    /**
     * The Protocol version. This will only change when there are changes made that
     * are not backwards compatible.
     */
    public static enum MeasurementProtocolVersion {
        /**
         * Version 1
         */
        V_1
    }

    /** The instance logger. This is initialised as the static logger. */
    private Logger instanceLogger = logger;

    /** The client parameters. */
    private final ClientParameters clientParameters;

    /** The URL builder. */
    private final IAnalyticsMeasurementProtocolURLBuilder builder;

    /** The dispatch mode. */
    private DispatchMode mode;

    /** The enabled flag. */
    private boolean enabled;

    /** The secure flag. Set to true to use HTTPS. */
    private boolean secure;

    /**
     * Create an instance.
     *
     * @param clientParameters The client parameters
     */
    public GoogleAnalyticsTracker(ClientParameters clientParameters) {
        this(clientParameters, DispatchMode.SINGLE_THREAD);
    }

    /**
     * Create an instance.
     *
     * @param clientParameters The client parameters
     * @param dispatchMode     The dispatch mode
     */
    public GoogleAnalyticsTracker(ClientParameters clientParameters, DispatchMode dispatchMode) {
        this(clientParameters, dispatchMode, MeasurementProtocolVersion.V_1);
    }

    /**
     * Create an instance.
     *
     * @param clientParameters The client parameters
     * @param dispatchMode     The dispatch mode
     * @param version          The GA version
     */
    public GoogleAnalyticsTracker(ClientParameters clientParameters, DispatchMode dispatchMode,
            MeasurementProtocolVersion version) {
        this.clientParameters = Objects.requireNonNull(clientParameters, "Client parameters is null");
        builder = createBuilder(version);
        enabled = true;
        setDispatchMode(dispatchMode);
    }

    /**
     * Sets the dispatch mode.
     *
     * @see DispatchMode
     * @param mode the mode to to put the tracker in. If this is null, the tracker
     *             defaults to {@link DispatchMode#SINGLE_THREAD}
     */
    public void setDispatchMode(DispatchMode mode) {
        if (mode == null)
            mode = DispatchMode.SINGLE_THREAD;
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
     * @see DispatchMode#SYNCHRONOUS
     */
    public boolean isSynchronous() {
        return mode == DispatchMode.SYNCHRONOUS;
    }

    /**
     * Convenience method to check if the tracker is in an asynchronous mode.
     *
     * @return true, if is asynchronous
     */
    public boolean isAsynchronous() {
        return mode.isAsynchronous();
    }

    /**
     * Convenience method to check if the tracker is in single-thread mode.
     *
     * @return true, if is single threaded
     * @see DispatchMode#SINGLE_THREAD
     */
    public boolean isSingleThreaded() {
        return mode == DispatchMode.SINGLE_THREAD;
    }

    /**
     * Convenience method to check if the tracker is in multi-thread mode.
     *
     * @return true, if is multi threaded
     * @see DispatchMode#MULTI_THREAD
     */
    public boolean isMultiThreaded() {
        return mode == DispatchMode.MULTI_THREAD;
    }

    /**
     * Sets to true to enable dispatching tracking requests.
     *
     * @param enabled true if enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Check if dispatching tracking requests from this tracker.
     *
     * <p>Note: This also returns {@code false} if there was an exception for any
     * tracking request that signals a major problem.
     *
     * @return true if enabled
     * @see #isDisabled()
     */
    public boolean isEnabled() {
        return enabled && !isDisabled();
    }

    /**
     * Checks if is using a secure connection (HTTPS).
     *
     * @return true if is secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets to true to use a secure connection (HTTPS).
     *
     * @param secure true if is secure
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Define the proxy to use for all GA tracking requests. You can pass
     * Proxy.NO_PROXY to explicitly use no proxy. Pass null to revert to the system
     * default mechanism for connecting.
     *
     * <p>Call this static method early (before creating any tracking requests).
     *
     * @param proxy The proxy to use
     */
    public static void setProxy(Proxy proxy) {
        GoogleAnalyticsTracker.proxy = proxy;
    }

    /**
     * Define the proxy to use for all GA tracking requests.
     *
     * <p>Call this static method early (before creating any tracking requests).
     *
     * <p>If no proxy can be set then this will revert the system to default
     * mechanism for connecting.
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
        setProxy((Proxy) null);
        return false;
    }

    /**
     * Wait for background tasks to complete.
     *
     * <p>This works in {@link DispatchMode#SINGLE_THREAD} and
     * {@link DispatchMode#MULTI_THREAD} mode.
     *
     * <p>Returns {@code true} if there are no background tasks remaining.
     *
     * <p>An {@link InterruptedException} on this thread causes this method to stop
     * waiting.
     *
     * @param timeoutMillis The maximum number of milliseconds to wait.
     * @return true if there are no background tasks remaining
     * @throws IllegalArgumentException if the timeout is negative
     * @see #hasBackgroundTasks()
     */
    public static boolean completeBackgroundTasks(long timeoutMillis) throws IllegalArgumentException {
        if (timeoutMillis < 0)
            throw new IllegalArgumentException("Timeout must be positive: " + timeoutMillis);
        final long endTime = System.currentTimeMillis() + timeoutMillis;
        while (hasBackgroundTasks()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                // Stop waiting
                return !hasBackgroundTasks();
            }
            if (System.currentTimeMillis() >= endTime) {
                // Timeout
                return !hasBackgroundTasks();
            }
        }
        // Normal exit: no tasks
        return true;
    }

    /**
     * Checks for background tasks.
     *
     * @return true, if there are background tasks
     */
    public static boolean hasBackgroundTasks() {
        return backgroundTasks.get() != 0;
    }

    /**
     * Send the tracking request to Google Analytics.
     *
     * @param requestParameters The request parameters
     * @return the dispatch status
     * @throws NullPointerException if request parameters are null
     */
    public DispatchStatus send(RequestParameters requestParameters) {
        return send(requestParameters, System.currentTimeMillis());
    }

    /**
     * Send the tracking request to Google Analytics.
     *
     * @param requestParameters The request parameters
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     * @return the dispatch status
     * @throws NullPointerException if request parameters are null
     */
    public DispatchStatus send(final RequestParameters requestParameters, final long timestamp) {
        if (!isEnabled()) {
            instanceLogger.fine("Ignoring tracking request, enabled is false");
            return DispatchStatus.IGNORED;
        }
        Objects.requireNonNull(requestParameters, "Request parameters cannot be null");

        DispatchStatus status;
        switch (mode) {
        case MULTI_THREAD:
            executeDispatchRequest(multiThreadExecutor, requestParameters, timestamp);
            status = DispatchStatus.RUNNING;
            break;

        case SYNCHRONOUS:
            // Do on the same thread
            if (dispatchRequest(requestParameters, timestamp))
                status = DispatchStatus.COMPLETE;
            else
                status = DispatchStatus.ERROR;
            break;

        case SINGLE_THREAD:
            // Fall through
        default: // Default to the single-thread
            executeDispatchRequest(singleThreadExecutor, requestParameters, timestamp);
            status = DispatchStatus.RUNNING;
            break;
        }
        return status;
    }

    /**
     * Submit the dispatch request using the provided executor.
     *
     * @param executorService   the executor service
     * @param requestParameters The request parameter data
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     */
    private void executeDispatchRequest(ExecutorService executorService, final RequestParameters requestParameters,
            final long timestamp) {
        // Increment the task count
        backgroundTasks.getAndIncrement();
        // Submit a new runnable
        executorService.execute(() -> {
            try {
                dispatchRequest(requestParameters, timestamp);
            } finally {
                // The task has run
                backgroundTasks.getAndDecrement();
            }
        });
    }

    /**
     * Send the parameters to Google Analytics using the Measurement Protocol. This
     * uses the HTTP POST method.
     *
     * @param requestParameters The request parameter data
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     * @return true, if successful
     */
    private boolean dispatchRequest(final RequestParameters requestParameters, final long timestamp) {
        return dispatchRequest(this, requestParameters, timestamp);
    }

    /**
     * Send the parameters to Google Analytics using the Measurement Protocol. This
     * uses the HTTP POST method.
     *
     * @param tracker           The tracker
     * @param requestParameters The request parameter data
     * @param timestamp         The timestamp when the hit was reported (in
     *                          milliseconds)
     * @return true, if successful
     */
    private static boolean dispatchRequest(GoogleAnalyticsTracker tracker, RequestParameters requestParameters,
            long timestamp) {
        if (isDisabled())
            return false;
        HttpURLConnection connection = null;
        final Logger instanceLogger = tracker.instanceLogger;
        try {
            final String parameters = tracker.builder.buildURL(tracker.clientParameters, requestParameters, timestamp);
            final URL url = new URL((tracker.secure) ? HTTPS_GOOGLE_ANALYTICS_URL : HTTP_GOOGLE_ANALYTICS_URL);
            connection = (HttpURLConnection) ((proxy == null) ? url.openConnection() : url.openConnection(proxy));
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            final byte[] out = parameters.getBytes(StandardCharsets.UTF_8);
            final int length = out.length;
            connection.setFixedLengthStreamingMode(length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.connect();
            try (final OutputStream os = connection.getOutputStream()) {
                os.write(out);
            }
            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Note: Valid on 31-Aug-2018
                // https://developers.google.com/analytics/devguides/collection/protocol/v1/validating-hits
                // "The Google Analytics Measurement Protocol does not return HTTP error codes"
                // This is unlikely to happen so log a warning.
                // If Google change their response in the future this logging will serve
                // as notice to update the code to do something more appropriate.
                instanceLogger.log(Level.WARNING, () -> {
                    return String.format("Error requesting url '%s', received response code %d", parameters,
                            responseCode);
                });
                // Don't disable the tracker. It may just be a user of this library
                // has built an invalid RequestParameters object so they should know
                // about this. Lots of log messages will be notice enough.
                // tracker.setEnabled(false);
            } else {
                instanceLogger.log(Level.FINE, () -> {
                    return String.format("Tracking success for url '%s'", parameters);
                });
                // This is a success. All other returns are false.
                return true;
            }
        } catch (final UnknownHostException e) {
            setLastIOException(e);
            // Occurs when disconnected from the Internet so this is not severe
            instanceLogger.log(Level.WARNING, () -> {
                return String.format("Unknown host: %s", e.getMessage());
            });
        } catch (final IOException e) {
            setLastIOException(e);
            // Log all others at a severe level
            instanceLogger.log(Level.SEVERE, () -> {
                return String.format("Error making tracking request: %s : %s", e.getClass().getSimpleName(),
                        e.getMessage());
            });
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    /**
     * Sets the last IO exception.
     *
     * <p>This will disable all tracking requests.
     *
     * @param e the last IO exception
     */
    private static void setLastIOException(IOException e) {
        lastIOException = e;
    }

    /**
     * Gets the last IO exception that occurred from a dispatch request.
     *
     * <p>If this is not {@code null} then all tracking is disabled as it is assumed
     * that all subsequent tracking requests will fail.
     *
     * @return the last IO exception
     */
    public static IOException getLastIOException() {
        return lastIOException;
    }

    /**
     * Checks if all tracking is disabled.
     *
     * <p>This is true if an exception occurred during a dispatch request, i.e. any
     * subsequent tracking requests will fail.
     *
     * <p>The state can be reset using {@link #clearLastIOException()} if another
     * attempt at tracking is to be attempted, e.g. if the reason for the problem
     * has been fixed.
     *
     * @return true, if is disabled
     * @see #getLastIOException()
     * @see #clearLastIOException()
     */
    public static boolean isDisabled() {
        return lastIOException != null;
    }

    /**
     * Clear the last IO exception.
     *
     * <p>Use this method to restart tracking.
     */
    public static void clearLastIOException() {
        setLastIOException(null);
    }

    /**
     * Creates the URL builder.
     *
     * @param version the version
     * @return the URL builder
     */
    private static IAnalyticsMeasurementProtocolURLBuilder createBuilder(MeasurementProtocolVersion version) {
        // Only one version at the moment. Test that it has been set.
        Objects.requireNonNull(version, "Version is null");
        return new AnalyticsMeasurementProtocolURLBuilder();
    }

    /**
     * Get the logger
     *
     * @return the logger
     */
    public Logger getLogger() {
        return instanceLogger;
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
        this.instanceLogger = logger;
    }

    /**
     * Reset the session (i.e. start a new session)
     */
    public void resetSession() {
        clientParameters.resetSession();
    }

    /**
     * Set the state of IP anonymisation
     *
     * @param anonymised True if the IP address of the sender will be anonymised
     */
    public void setAnonymised(boolean anonymised) {
        clientParameters.setAnonymised(anonymised);
    }
}
