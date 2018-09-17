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

import uk.ac.sussex.gdsc.analytics.GoogleAnalyticsClient.Builder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.PartialBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.ProtocolVersionParameter;
import uk.ac.sussex.gdsc.analytics.parameters.SessionControlParameter;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class GoogleAnalyticsTrackerTest {

  private final String trackingId = "UA-1234-5";
  private final String trackingId2 = "UA-4321-5";
  private final String clientId = "123e4567-e89b-12d3-a456-426655440000";
  private final String userId = "Mr. Test";

  @SuppressWarnings("unused")
  @Test
  public void testBuilder() {
    // Can build with defaults
    Builder builder = GoogleAnalyticsClient.createBuilder(trackingId);
    GoogleAnalyticsClient ga = builder.build();

    // Test getters and setters
    builder = GoogleAnalyticsClient.createBuilder(trackingId);

    Assertions.assertEquals(trackingId, builder.getTrackingId());
    builder.setTrackingId(trackingId2);
    Assertions.assertEquals(trackingId2, builder.getTrackingId());

    Assertions.assertNull(builder.getClientId());
    builder.setClientId(clientId);
    Assertions.assertEquals(clientId, builder.getClientId());
    builder.setClientId(UUID.fromString(clientId));
    Assertions.assertEquals(clientId, builder.getClientId());

    builder.setUserId(userId);
    Assertions.assertEquals(userId, builder.getUserId());

    int threadCount = builder.getThreadCount() + 1;
    builder.setThreadCount(threadCount);
    Assertions.assertEquals(threadCount, builder.getThreadCount());

    int threadPriority = builder.getThreadPriority();
    builder.setThreadPriority(threadPriority);
    Assertions.assertEquals(threadPriority, builder.getThreadPriority());

    builder.setExecutorService(null);
    ExecutorService executorService = builder.getOrCreateExecutorService();
    Assertions.assertSame(executorService, builder.getOrCreateExecutorService());
    builder.setThreadCount(0);
    builder.setExecutorService(null);
    ExecutorService executorService2 = builder.getOrCreateExecutorService();
    Assertions.assertNotNull(executorService2);
    Assertions.assertNotSame(executorService, executorService2);

    builder.setHitDispatcher(null);
    HitDispatcher hitDispatcher = builder.getOrCreateHitDispatcher();
    Assertions.assertSame(hitDispatcher, builder.getOrCreateHitDispatcher());

    PartialBuilder<Builder> perHitParameters = builder.getOrCreatePerHitParameters();
    Assertions.assertSame(perHitParameters, builder.getOrCreatePerHitParameters());
    builder.setPerHitParameters(ProtocolVersionParameter.V1);
    Assertions.assertNotSame(perHitParameters, builder.getOrCreatePerHitParameters());
    Assertions.assertEquals("v=1", builder.getOrCreatePerHitParameters().build().format());

    PartialBuilder<Builder> perSessionParameters = builder.getOrCreatePerSessionParameters();
    Assertions.assertSame(perSessionParameters, builder.getOrCreatePerSessionParameters());
    builder.setPerSessionParameters(SessionControlParameter.START);
    Assertions.assertNotSame(perSessionParameters, builder.getOrCreatePerSessionParameters());
    Assertions.assertEquals("sc=start", builder.getOrCreatePerSessionParameters().build().format());

    builder.setDebug(true);
    Assertions.assertTrue(builder.isDebug());
    builder.setDebug(true); // Just to make coverage
    builder.setDebug(false);
    Assertions.assertFalse(builder.isDebug());

    builder.setSecure(true);
    Assertions.assertTrue(builder.isSecure());
    builder.setSecure(true); // Just to make coverage
    builder.setSecure(false);
    Assertions.assertFalse(builder.isSecure());

    long sessionTimeout = builder.getSessionTimeout() + 10;
    builder.setSessionTimeout(sessionTimeout);
    Assertions.assertEquals(sessionTimeout, builder.getSessionTimeout());

    // Build and test the client has the same settings

    // Hit all edge cases of build

  }

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
