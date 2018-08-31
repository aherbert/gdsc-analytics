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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import uk.ac.sussex.gdsc.analytics.GoogleAnalyticsTracker.DispatchMode;
import uk.ac.sussex.gdsc.analytics.GoogleAnalyticsTracker.DispatchStatus;

/**
 * This uses the mockito framework to test the dispatch of GoogleAnalytics
 * requests
 */
@SuppressWarnings("javadoc")
public class GoogleAnalyticsTrackerSendTest {
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";

    // Get the default logger
    private static final Logger gaLogger = Logger.getLogger(GoogleAnalyticsTracker.class.getName());

    // Adapted from https://claritysoftware.co.uk/mocking-javas-url-with-mockito/

    /**
     * {@link URLStreamHandler} that allows us to control the {@link URLConnection
     * URLConnections} that are returned by {@link URL URLs} in the code under test.
     */
    public static class HttpUrlStreamHandler extends URLStreamHandler {

        private final Map<URL, URLConnection> connections = new HashMap<>();
        private final Map<URL, URLConnection> proxyConnections = new HashMap<>();
        private final BlockingQueue<URLConnection> queue = new LinkedBlockingQueue<>();
        private boolean fastMode = false;

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return getConnection(url, connections);
        }

        @Override
        protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
            return getConnection(url, proxyConnections);
        }

        /**
         * Gets the connection.
         *
         * <p> If the fast mode is enabled then the bext connection from the queue is
         * returned. When the queue is empty the fast mode is disabled.
         *
         * @param map the map of connections for each URL
         * @return the connection
         * @throws IOException
         */
        private URLConnection getConnection(URL url, Map<URL, URLConnection> map) throws IOException {
            if (fastMode) {
                final URLConnection conn = queue.poll();
                if (conn != null)
                    return conn;
                fastMode = false;
            }
            URLConnection conn = map.get(url);
            if (conn == null)
                throw new IOException("Mock no openConnection()");
            return conn;
        }

        public void resetConnections() {
            // System.out.println("Reset");
            connections.clear();
            proxyConnections.clear();
            queue.clear();
            fastMode = false;
        }

        /**
         * Adds the connection for the URL.
         *
         * @param url   the url
         * @param conn  the connection
         * @param proxy True if this is a connection using a proxy
         */
        public void addConnection(URL url, URLConnection conn, boolean proxy) {
            // System.out.printf("add %s %b thread = %d : %s\n", url.toString(), proxy,
            // Thread.currentThread().getId(),
            // Integer.toHexString(System.identityHashCode(conn)));
            if (proxy)
                proxyConnections.put(url, conn);
            else
                connections.put(url, conn);
        }

        /**
         * Adds a fast connection. The connection will be returned from a queue and
         * ignores the URL.
         *
         * @param conn the connection
         */
        public void addFastConnection(URLConnection conn) {
            fastMode = true;
            queue.add(conn);
        }
    }

    /** The http url stream handler for non-secure connections. */
    private static HttpUrlStreamHandler httpUrlStreamHandler;
    /** The http url stream handler for secure connections. */
    private static HttpUrlStreamHandler httpsUrlStreamHandler;

    @BeforeAll
    public static void setupURLStreamHandlerFactory() {
        // Allows for mocking URL connections
        final URLStreamHandlerFactory urlStreamHandlerFactory = Mockito.mock(URLStreamHandlerFactory.class);

        // Note: Setting this is a one time operation per VM.
        // So this will prevent any other java code from connecting
        // with a URL to a valid Internet resource.
        URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);

        httpUrlStreamHandler = new HttpUrlStreamHandler();
        httpsUrlStreamHandler = new HttpUrlStreamHandler();
        // Handle both secure and non-secure HTTP requests
        Mockito.when(urlStreamHandlerFactory.createURLStreamHandler("http")).thenReturn(httpUrlStreamHandler);
        Mockito.when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(httpsUrlStreamHandler);
    }

    @BeforeEach
    public void reset() {
        httpUrlStreamHandler.resetConnections();
    }

    @Test
    public void testHttpSynchronousSend() throws Exception {
        // This allows the logging for a tracking success to be hit
        final Level l = gaLogger.getLevel();
        gaLogger.setLevel(Level.FINE);
        testSend(false, false, DispatchMode.SYNCHRONOUS);
        gaLogger.setLevel(l);
    }

    @Test
    public void testHttpSingleThreadSend() throws Exception {
        testSend(false, false, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpMultiThreadSend() throws Exception {
        testSend(false, false, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpsSynchronousSend() throws Exception {
        testSend(true, false, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpsSingleThreadSend() throws Exception {
        testSend(true, false, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpsMultiThreadSend() throws Exception {
        testSend(true, false, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpProxySynchronousSend() throws Exception {
        testSend(false, true, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpProxySingleThreadSend() throws Exception {
        testSend(false, true, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpProxyMultiThreadSend() throws Exception {
        testSend(false, true, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpsProxySynchronousSend() throws Exception {
        testSend(true, true, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpsProxySingleThreadSend() throws Exception {
        testSend(true, true, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpsProxyMultiThreadSend() throws Exception {
        testSend(true, true, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpSynchronousSendWithBadResponse() throws Exception {
        testSend(true, true, DispatchMode.SYNCHRONOUS, HttpURLConnection.HTTP_INTERNAL_ERROR, false);
    }

    @Test
    public void testHttpSynchronousSendWithIOException() throws Exception {
        testSend(true, true, DispatchMode.SYNCHRONOUS, HttpURLConnection.HTTP_OK, true);
    }

    private void testSend(boolean secure, boolean proxy, DispatchMode mode) throws Exception {
        testSend(secure, proxy, mode, HttpURLConnection.HTTP_OK, false);
    }

    @SuppressWarnings({ "unchecked" })
    private void testSend(boolean secure, boolean proxy, DispatchMode mode, int responseCode, boolean ioException)
            throws Exception {

        final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        final HttpURLConnection urlConnection = createHttpUrlConnection(responseCode, out);

        String host = addConnection(urlConnection, secure, proxy);

        // For edge case testing
        Logger logger = gaLogger;
        if (responseCode != HttpURLConnection.HTTP_OK)
            logger = Mockito.spy(logger);
        if (ioException) {
            Mockito.doThrow(new IOException("Mock IO exception")).when(urlConnection).connect();
            logger = Mockito.spy(logger);
        }

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, mode);

        // Configure
        tracker.setSecure(secure);
        // We can use NO_PROXY here, not a mock proxy.
        GoogleAnalyticsTracker.setProxy((proxy) ? Proxy.NO_PROXY : null);
        // tracker.setDispatchMode(mode);
        tracker.setLogger(logger);

        final RequestParameters rp = createRequest("path 1", "title 2");

        final DispatchStatus status = tracker.send(rp);

        if (mode.isAsynchronous()) {
            // Wait for the request to be queued
            Thread.sleep(100);
            Assertions.assertThat(GoogleAnalyticsTracker.completeBackgroundTasks(1000)).isEqualTo(true);
        }

        // Edge case testing. These should be logged.
        if (responseCode != HttpURLConnection.HTTP_OK) {
            Mockito.verify(logger, Mockito.times(1)).log(ArgumentMatchers.any(Level.class),
                    ArgumentMatchers.any(Supplier.class));
            if (mode == DispatchMode.SYNCHRONOUS)
                Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
            // Not expected to work so don't test
            return;
        }
        if (ioException) {
            Mockito.verify(logger, Mockito.times(1)).log(ArgumentMatchers.any(Level.class),
                    ArgumentMatchers.any(Supplier.class));
            if (mode == DispatchMode.SYNCHRONOUS)
                Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
            Assertions.assertThat(GoogleAnalyticsTracker.isDisabled()).isEqualTo(true);
            GoogleAnalyticsTracker.clearLastIOException();
            // Not expected to work so don't test
            return;
        }

        // Check the output stream
        Assertions.assertThat(out.size()).isNotEqualTo(0);

        // Check interaction with the connection
        Mockito.verify(urlConnection, Mockito.times(1)).setRequestMethod("POST");
        Mockito.verify(urlConnection, Mockito.times(1)).setDoOutput(true);
        Mockito.verify(urlConnection, Mockito.times(1)).setUseCaches(false);
        Mockito.verify(urlConnection, Mockito.times(1)).setFixedLengthStreamingMode(out.size());

        // Check the content of the POST
        final String parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);

        //@formatter:off
        Assertions.assertThat(new URL(host + "?" + parameters))
        .hasParameter("tid", trackingId)
        .hasParameter("cid", clientId)
        .hasParameter("an", applicationName)
        .hasParameter("dp", rp.getDocumentPath())
        .hasParameter("dt", rp.getDocumentTitle())
        ;
        //@formatter:on
    }

    private static String addConnection(HttpURLConnection urlConnection, boolean secure, boolean proxy)
            throws MalformedURLException {
        String host;
        if (secure) {
            host = GoogleAnalyticsTracker.HTTPS_GOOGLE_ANALYTICS_URL;
            httpsUrlStreamHandler.addConnection(new URL(host), urlConnection, proxy);
        } else {
            host = GoogleAnalyticsTracker.HTTP_GOOGLE_ANALYTICS_URL;
            httpUrlStreamHandler.addConnection(new URL(host), urlConnection, proxy);
        }
        return host;
    }

    private static RequestParameters createRequest(String documentPath, String documentTitle) {
        final RequestParameters rp = new RequestParameters(HitType.PAGEVIEW);
        rp.setDocumentPath(documentPath);
        rp.setDocumentTitle(documentTitle);
        return rp;
    }

    private static HttpURLConnection createHttpUrlConnection(int responseCode, ByteArrayOutputStream out)
            throws IOException {
        // The mock object will not do anything
        final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);

        // The response
        Mockito.doReturn(responseCode).when(urlConnection).getResponseCode();

        // Provide space for the POST request
        Mockito.doReturn(out).when(urlConnection).getOutputStream();
        return urlConnection;
    }

    @Test
    public void testConsecutiveSend() throws Exception {

        // 1. Synchronous
        // 2. Single-thread
        // 3. Multi-thread
        final List<ByteArrayOutputStream> output = setupFastConnections(3);

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp);

        // Ignored
        tracker.setEnabled(false);
        Assertions.assertThat(tracker.send(createRequest("p", "t"))).isEqualTo(DispatchStatus.IGNORED);

        tracker.setEnabled(true);

        final List<RequestParameters> requests = setupRequests(output.size());
        int c = 0;

        // 1. Syncrhonous OK
        tracker.setDispatchMode(DispatchMode.SYNCHRONOUS);
        Assertions.assertThat(tracker.send(requests.get(c++))).isEqualTo(DispatchStatus.COMPLETE);

        // 2. Single-thread
        tracker.setDispatchMode(DispatchMode.SINGLE_THREAD);
        Assertions.assertThat(tracker.send(requests.get(c++))).isEqualTo(DispatchStatus.RUNNING);

        // 3. Single-thread (background running)
        tracker.setDispatchMode(DispatchMode.MULTI_THREAD);
        Assertions.assertThat(tracker.send(requests.get(c++))).isEqualTo(DispatchStatus.RUNNING);

        Assertions.assertThat(GoogleAnalyticsTracker.completeBackgroundTasks(1000)).isEqualTo(true);

        // Check the output streams have all been used
        final HashSet<String> set = new HashSet<>();
        for (final ByteArrayOutputStream out : output) {
            Assertions.assertThat(out.size()).isNotEqualTo(0);
            final String parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
            // gaLogger.info(parameters);
            final String dp = getParameter(parameters, "dp");
            final String dt = getParameter(parameters, "dt");
            Assertions.assertThat(set.add(dp + dt)).isTrue();
        }
    }

    private static List<ByteArrayOutputStream> setupFastConnections(int size) throws IOException {
        final List<ByteArrayOutputStream> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            final HttpURLConnection urlConnection = createHttpUrlConnection(HttpURLConnection.HTTP_OK, out);
            httpUrlStreamHandler.addFastConnection(urlConnection);
            list.add(out);
        }
        return list;
    }

    private static List<RequestParameters> setupRequests(int size) {
        final List<RequestParameters> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            list.add(createRequest("p" + i, "t" + i));
        return list;
    }

    private static String getParameter(String parameters, String string) {
        final int i1 = parameters.indexOf(string);
        Assertions.assertThat(i1).isNotEqualTo(-1);
        final int i2 = parameters.indexOf('&', i1);
        Assertions.assertThat(i2).isNotEqualTo(-1).isNotEqualTo(i1);
        return parameters.substring(i1, i2);
    }

    @Test
    public void testMultiThreadBatchSend() throws Exception {
        testBatchSend(50, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testSingleThreadBatchSend() throws Exception {
        testBatchSend(50, DispatchMode.SINGLE_THREAD);
    }

    private void testBatchSend(int size, DispatchMode mode) throws Exception {

        final List<ByteArrayOutputStream> output = setupFastConnections(size);

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, mode);

        final List<RequestParameters> requests = setupRequests(output.size());
        for (final RequestParameters rp : requests)
            tracker.send(rp);

        // Hope that we can hit the timeout
        final boolean notFinished = GoogleAnalyticsTracker.completeBackgroundTasks(100);
        if (notFinished)
            Assertions.assertThat(GoogleAnalyticsTracker.completeBackgroundTasks(10000)).isEqualTo(true);

        // Check the output streams have all been used
        final HashSet<String> set = new HashSet<>();
        for (final ByteArrayOutputStream out : output) {
            Assertions.assertThat(out.size()).isNotEqualTo(0);
            final String parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
            // gaLogger.info(parameters);
            final String dp = getParameter(parameters, "dp");
            final String dt = getParameter(parameters, "dt");
            Assertions.assertThat(set.add(dp + dt)).isTrue();
        }
    }

    /**
     * This test attempts to create an error after tasks have been submitted to the
     * queue so that they should be ignored when the single thread gets to process
     * them.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSingleThreadBatchSendWithError() throws Exception {

        int set1 = 5, set2 = 5;

        // OK connections
        final List<ByteArrayOutputStream> output = setupFastConnections(set1);

        // One bad one
        final ByteArrayOutputStream badOut = new ByteArrayOutputStream(1024);
        final HttpURLConnection urlConnection = createHttpUrlConnection(HttpURLConnection.HTTP_OK, badOut);
        Mockito.doThrow(IOException.class).when(urlConnection).connect();
        httpUrlStreamHandler.addFastConnection(urlConnection);
        output.add(badOut);

        // More OK connections
        final List<ByteArrayOutputStream> output2 = setupFastConnections(set2);

        output.addAll(output2);

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, DispatchMode.SINGLE_THREAD);

        final List<RequestParameters> requests = setupRequests(output.size());
        final List<DispatchStatus> status = new ArrayList<>();
        for (final RequestParameters rp : requests) {
            status.add(tracker.send(rp));
        }

        // The threads should not be able to process everything
        Assertions.assertThat(GoogleAnalyticsTracker.hasNoBackgroundTasks()).isEqualTo(false);
        Assertions.assertThat(GoogleAnalyticsTracker.completeBackgroundTasks(10000)).isEqualTo(true);
        Assertions.assertThat(GoogleAnalyticsTracker.hasNoBackgroundTasks()).isEqualTo(true);

        GoogleAnalyticsTracker.clearLastIOException();

        // This test is only valid if more items were queued
        // before the bad connection occurs to disabled the tracker.
        // Count each status. Either ignored or running is expected.
        // running should be more than set1.
        int ignored = 0, running = 0, other = 0;
        for (DispatchStatus s : status) {
            switch (s) {
            case IGNORED:
                ignored++;
                break;
            case RUNNING:
                running++;
                break;
            default:
                other++;
                break;
            }
        }

        gaLogger.fine(String.format("Ignored = %d, Running = %d, Other = %d", ignored, running, other));

        if (running <= set1) {
            // Test not valid
            return;
        }

        // Check not all the output streams have all been used
        final HashSet<String> set = new HashSet<>();
        for (final ByteArrayOutputStream out : output) {
            if (out.size() == 0) {
                continue;
            }
            // Check these all were unique requests
            final String parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
            final String dp = getParameter(parameters, "dp");
            final String dt = getParameter(parameters, "dt");
            Assertions.assertThat(set.add(dp + dt)).isTrue();
        }

        // Expecting to only process the first set, the rest should be ignored.
        Assertions.assertThat(set.size()).isEqualTo(set1);
    }

    /**
     * Test a failed connection disables. This is handled differently to any other
     * IO exception so has a separate test.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFailedConnectDisables() throws Exception {

        final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        final HttpURLConnection urlConnection = createHttpUrlConnection(HttpURLConnection.HTTP_OK, out);

        IOException ex = new UnknownHostException("Mock no connect()");
        Mockito.doThrow(ex).when(urlConnection).connect();

        addConnection(urlConnection, false, false);

        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, DispatchMode.SYNCHRONOUS);
        final RequestParameters rp = createRequest("path", "title");

        DispatchStatus status = tracker.send(rp);
        Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
        Assertions.assertThat(tracker.isEnabled()).isEqualTo(false);
        Assertions.assertThat(GoogleAnalyticsTracker.isDisabled()).isEqualTo(true);
        Assertions.assertThat(GoogleAnalyticsTracker.getLastIOException()).isEqualTo(ex);
        GoogleAnalyticsTracker.clearLastIOException();
        Assertions.assertThat(GoogleAnalyticsTracker.isDisabled()).isEqualTo(false);
        Assertions.assertThat(GoogleAnalyticsTracker.getLastIOException()).isNull();
    }

    /**
     * Test a failed connection disables. This is handled differently to any other
     * IO exception so has a separate test.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFailedOpenConnectionDisables() throws Exception {

        // Don't set up a URLConnection. The mock handler will throw an IOException.

        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, DispatchMode.SYNCHRONOUS);
        final RequestParameters rp = createRequest("path", "title");

        DispatchStatus status = tracker.send(rp);
        Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
        Assertions.assertThat(tracker.isEnabled()).isEqualTo(false);
        Assertions.assertThat(GoogleAnalyticsTracker.isDisabled()).isEqualTo(true);
        Assertions.assertThat(GoogleAnalyticsTracker.getLastIOException()).isNotNull();
        GoogleAnalyticsTracker.clearLastIOException();
        Assertions.assertThat(GoogleAnalyticsTracker.isDisabled()).isEqualTo(false);
        Assertions.assertThat(GoogleAnalyticsTracker.getLastIOException()).isNull();
    }
}
