/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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

@SuppressWarnings("javadoc")
public class GoogleAnalyticsTrackerTest {
  private final String trackingId = "AAA-123-456";
  private final String clientId = "Anything";
  private final String applicationName = "Test";

  // @SuppressWarnings("unused")
  // @Test
  // public void testConstructor() {
  // final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
  //
  // GoogleAnalyticsClient tracker = new GoogleAnalyticsClient(cp);
  // Assertions.assertNotNull(tracker);
  // Assertions.assertTrue(tracker.isIgnore());
  // Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
  //
  // // Overloaded constructor
  // final DispatchMode mode = DispatchMode.MULTI_THREAD;
  // tracker = new GoogleAnalyticsClient(cp, mode);
  // Assertions.assertEquals(mode, tracker.getDispatchMode());
  //
  // final DispatchMode mode2 = DispatchMode.SINGLE_THREAD;
  // final MeasurementProtocolVersion version = MeasurementProtocolVersion.V_1;
  // tracker = new GoogleAnalyticsClient(cp, mode2, version);
  // Assertions.assertEquals(mode2, tracker.getDispatchMode());
  //
  // // Require some arguments (not dispatch mode)
  // Assertions.assertThrows(NullPointerException.class, () -> {
  // new GoogleAnalyticsClient(null, mode, version);
  // });
  // Assertions.assertThrows(NullPointerException.class, () -> {
  // new GoogleAnalyticsClient(cp, mode, null);
  // });
  // }
  //
  // @Test
  // public void testProperties() {
  // final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
  //
  // final GoogleAnalyticsClient tracker = new GoogleAnalyticsClient(cp);
  //
  // tracker.setDispatchMode(DispatchMode.SYNCHRONOUS);
  // Assertions.assertEquals(DispatchMode.SYNCHRONOUS, tracker.getDispatchMode());
  // Assertions.assertTrue(tracker.isSynchronous());
  // Assertions.assertFalse(tracker.isAsynchronous());
  // Assertions.assertFalse(tracker.isSingleThreaded());
  // Assertions.assertFalse(tracker.isMultiThreaded());
  //
  // tracker.setDispatchMode(DispatchMode.SINGLE_THREAD);
  // Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
  // Assertions.assertFalse(tracker.isSynchronous());
  // Assertions.assertTrue(tracker.isAsynchronous());
  // Assertions.assertTrue(tracker.isSingleThreaded());
  // Assertions.assertFalse(tracker.isMultiThreaded());
  //
  // tracker.setDispatchMode(DispatchMode.MULTI_THREAD);
  // Assertions.assertEquals(DispatchMode.MULTI_THREAD, tracker.getDispatchMode());
  // Assertions.assertFalse(tracker.isSynchronous());
  // Assertions.assertTrue(tracker.isAsynchronous());
  // Assertions.assertFalse(tracker.isSingleThreaded());
  // Assertions.assertTrue(tracker.isMultiThreaded());
  //
  // // Test default to single thread.
  // tracker.setDispatchMode(null);
  // Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
  // Assertions.assertFalse(tracker.isSynchronous());
  // Assertions.assertTrue(tracker.isAsynchronous());
  // Assertions.assertTrue(tracker.isSingleThreaded());
  // Assertions.assertFalse(tracker.isMultiThreaded());
  //
  // // On by default
  // Assertions.assertTrue(tracker.isIgnore());
  // tracker.setIgnore(false);
  // Assertions.assertFalse(tracker.isIgnore());
  //
  // // Off by default
  // Assertions.assertFalse(tracker.isSecure());
  // tracker.setSecure(true);
  // Assertions.assertTrue(tracker.isSecure());
  //
  // // Test the properties of the client get invoked
  // final boolean[] resetSession = new boolean[1];
  // final boolean[] setAnonymised = new boolean[1];
  // final ClientParameters fakeCP = new ClientParameters(trackingId, clientId, applicationName) {
  // @Override
  // public void resetSession() {
  // resetSession[0] = true;
  // }
  //
  // @Override
  // public void setAnonymised(boolean anonymised) {
  // setAnonymised[0] = true;
  // }
  // };
  // new GoogleAnalyticsClient(fakeCP).resetSession();
  // Assertions.assertTrue(resetSession[0]);
  // new GoogleAnalyticsClient(fakeCP).setAnonymised(true);
  // Assertions.assertTrue(setAnonymised[0]);
  // }
  //
  // @Test
  // public void testProxy() {
  // GoogleAnalyticsClient.setProxy((Proxy) null);
  // GoogleAnalyticsClient.setProxy(Proxy.NO_PROXY);
  //
  // // Test various proxy addresses
  // // Valid
  // Assertions.assertTrue(GoogleAnalyticsClient.setProxy("http://localhost:80"));
  // Assertions.assertTrue(GoogleAnalyticsClient.setProxy("https://localhost:80"));
  // Assertions.assertTrue(GoogleAnalyticsClient.setProxy("localhost:80"));
  // Assertions.assertTrue(GoogleAnalyticsClient.setProxy("https://localhost:80/more/stuff"));
  //
  // // Invalid
  // Assertions.assertFalse(GoogleAnalyticsClient.setProxy((String) null));
  // Assertions.assertFalse(GoogleAnalyticsClient.setProxy(""));
  // Assertions.assertFalse(GoogleAnalyticsClient.setProxy("localhost"));
  // Assertions.assertFalse(GoogleAnalyticsClient.setProxy("http://localhost"));
  // Assertions.assertFalse(GoogleAnalyticsClient.setProxy("http://localhost :80"));
  //
  // // Reset
  // GoogleAnalyticsClient.setProxy(Proxy.NO_PROXY);
  // }
  //
  // @Test
  // public void testCompleteBackgroundTasksThrows() {
  // Assertions.assertThrows(IllegalArgumentException.class, () -> {
  // GoogleAnalyticsClient.completeBackgroundTasks(-1);
  // });
  // }
}
