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
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
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
public class GoogleAnalyticsTrackerDispatchTest {
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
        protected URLConnection openConnection(URL url) {
            return getConnection(url, connections);
        }

        @Override
        protected URLConnection openConnection(URL url, Proxy proxy) {
            return getConnection(url, proxyConnections);
        }

        /**
         * Gets the connection.
         *
         * <p>If the fast mode is enabled then the bext connection from the queue is
         * returned. When the queue is empty the fast mode is disabled.
         *
         * @param map the map of connections for each URL
         * @return the connection
         */
        private URLConnection getConnection(URL url, Map<URL, URLConnection> map) {
            if (fastMode) {
                final URLConnection conn = queue.poll();
                if (conn != null)
                    return conn;
                fastMode = false;
            }
            return map.get(url);
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
    public void testHttpSynchronousDispatch() throws Exception {
        // This allows the logging for a tracking success to be hit
        final Level l = gaLogger.getLevel();
        gaLogger.setLevel(Level.FINE);
        testDispatch(false, false, DispatchMode.SYNCHRONOUS);
        gaLogger.setLevel(l);
    }

    @Test
    public void testHttpSingleThreadDispatch() throws Exception {
        testDispatch(false, false, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpMultiThreadDispatch() throws Exception {
        testDispatch(false, false, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpsSynchronousDispatch() throws Exception {
        testDispatch(true, false, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpsSingleThreadDispatch() throws Exception {
        testDispatch(true, false, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpsMultiThreadDispatch() throws Exception {
        testDispatch(true, false, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpProxySynchronousDispatch() throws Exception {
        testDispatch(false, true, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpProxySingleThreadDispatch() throws Exception {
        testDispatch(false, true, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpProxyMultiThreadDispatch() throws Exception {
        testDispatch(false, true, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpsProxySynchronousDispatch() throws Exception {
        testDispatch(true, true, DispatchMode.SYNCHRONOUS);
    }

    @Test
    public void testHttpsProxySingleThreadDispatch() throws Exception {
        testDispatch(true, true, DispatchMode.SINGLE_THREAD);
    }

    @Test
    public void testHttpsProxyMultiThreadDispatch() throws Exception {
        testDispatch(true, true, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testHttpSynchronousDispatchWithBadResponse() throws Exception {
        testDispatch(true, true, DispatchMode.SYNCHRONOUS, HttpURLConnection.HTTP_INTERNAL_ERROR, false);
    }

    @Test
    public void testHttpSynchronousDispatchWithIOException() throws Exception {
        testDispatch(true, true, DispatchMode.SYNCHRONOUS, HttpURLConnection.HTTP_OK, true);
    }

    private void testDispatch(boolean secure, boolean proxy, DispatchMode mode) throws Exception {
        testDispatch(secure, proxy, mode, HttpURLConnection.HTTP_OK, false);
    }

    @SuppressWarnings({ "unchecked" })
    private void testDispatch(boolean secure, boolean proxy, DispatchMode mode, int responseCode, boolean ioException)
            throws Exception {

        final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        final HttpURLConnection urlConnection = createHttpUrlConnection(responseCode, out);

        String host;
        if (secure) {
            host = GoogleAnalyticsTracker.HTTPS_GOOGLE_ANALYTICS_URL;
            httpsUrlStreamHandler.addConnection(new URL(host), urlConnection, proxy);
        } else {
            host = GoogleAnalyticsTracker.HTTP_GOOGLE_ANALYTICS_URL;
            httpUrlStreamHandler.addConnection(new URL(host), urlConnection, proxy);
        }

        // For edge case testing
        Logger logger = gaLogger;
        if (responseCode != HttpURLConnection.HTTP_OK)
            logger = Mockito.spy(logger);
        if (ioException) {
            Mockito.doThrow(IOException.class).when(urlConnection).connect();
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

        final DispatchStatus status = tracker.makeCustomRequest(rp);

        if (mode.isAsynchronous()) {
            // Wait for the request to be queued
            Thread.sleep(100);
            Assertions.assertThat(GoogleAnalyticsTracker.completeBackgroundTasks(1000)).isEqualTo(true);
        }

        // Edge case testing. These should be logged.
        if (responseCode != HttpURLConnection.HTTP_OK) {
            Mockito.verify(logger, Mockito.times(1)).severe(ArgumentMatchers.any(Supplier.class));
            if (mode == DispatchMode.SYNCHRONOUS)
                Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
            // Not expected to work so don't test
            return;
        }
        if (ioException) {
            Mockito.verify(logger, Mockito.times(1)).log(ArgumentMatchers.any(Level.class),
                    ArgumentMatchers.anyString(), ArgumentMatchers.any(IOException.class));
            if (mode == DispatchMode.SYNCHRONOUS)
                Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);
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
    public void testMakeRequest() throws Exception {

        // 1. Synchronous
        // 2. Single-thread
        // 3. Multi-thread
        final List<ByteArrayOutputStream> output = setupFastConnections(3);

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp);

        // Ignored
        tracker.setEnabled(false);
        Assertions.assertThat(tracker.makeCustomRequest(createRequest("p", "t"))).isEqualTo(DispatchStatus.IGNORED);

        tracker.setEnabled(true);

        final List<RequestParameters> requests = setupRequests(output.size());
        int c = 0;

        // 1. Syncrhonous OK
        tracker.setDispatchMode(DispatchMode.SYNCHRONOUS);
        Assertions.assertThat(tracker.makeCustomRequest(requests.get(c++))).isEqualTo(DispatchStatus.COMPLETE);

        // 2. Single-thread
        tracker.setDispatchMode(DispatchMode.SINGLE_THREAD);
        Assertions.assertThat(tracker.makeCustomRequest(requests.get(c++))).isEqualTo(DispatchStatus.RUNNING);

        // 3. Single-thread (background running)
        tracker.setDispatchMode(DispatchMode.MULTI_THREAD);
        Assertions.assertThat(tracker.makeCustomRequest(requests.get(c++))).isEqualTo(DispatchStatus.RUNNING);

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
    public void testMultiThreadRequests() throws Exception {
        testBackgroundThreadRequests(50, DispatchMode.MULTI_THREAD);
    }

    @Test
    public void testSingleThreadRequests() throws Exception {
        testBackgroundThreadRequests(50, DispatchMode.SINGLE_THREAD);
    }

    private void testBackgroundThreadRequests(int size, DispatchMode mode) throws Exception {

        final List<ByteArrayOutputStream> output = setupFastConnections(size);

        // Send tracking request
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp, mode);

        final List<RequestParameters> requests = setupRequests(output.size());
        for (final RequestParameters rp : requests)
            tracker.makeCustomRequest(rp);

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
}
