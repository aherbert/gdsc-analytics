/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2020 Alex Herbert
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
import uk.ac.sussex.gdsc.analytics.parameters.CustomParameterSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.HitType;
import uk.ac.sussex.gdsc.analytics.parameters.NoIndexTextParameter;
import uk.ac.sussex.gdsc.analytics.parameters.OneIndexIntParameter;
import uk.ac.sussex.gdsc.analytics.parameters.OneIndexTextParameter;
import uk.ac.sussex.gdsc.analytics.parameters.ParameterSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.PartialBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.ProtocolSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.ProtocolVersionParameter;
import uk.ac.sussex.gdsc.analytics.parameters.SessionControlParameter;
import uk.ac.sussex.gdsc.analytics.parameters.UrlEncoderHelper;
import uk.ac.sussex.gdsc.analytics.parameters.ValueType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("javadoc")
public class GoogleAnalyticsClientTest {

  private static final Logger logger = Logger.getLogger(GoogleAnalyticsClientTest.class.getName());
  private static final Level debugLevel = Level.FINE;

  private final String trackingId = "UA-12345-6";
  private final String trackingId2 = "UA-54321-6";
  private final String clientId = "123e4567-e89b-12d3-a456-426655440000";
  private final String userId = "Mr. Test";

  @Test
  public void testBuilder() {
    // Can build with defaults
    Builder builder = GoogleAnalyticsClient.newBuilder(trackingId);
    builder.build();

    // Test getters and setters
    builder = GoogleAnalyticsClient.newBuilder(trackingId);

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

    final int threadCount = builder.getThreadCount() + 1;
    builder.setThreadCount(threadCount);
    Assertions.assertEquals(threadCount, builder.getThreadCount());

    final int threadPriority = builder.getThreadPriority();
    builder.setThreadPriority(threadPriority);
    Assertions.assertEquals(threadPriority, builder.getThreadPriority());

    builder.setExecutorService(null);
    final ExecutorService executorService = builder.getOrCreateExecutorService();
    Assertions.assertSame(executorService, builder.getOrCreateExecutorService());
    builder.setThreadCount(0);
    builder.setExecutorService(null);
    final ExecutorService executorService2 = builder.getOrCreateExecutorService();
    Assertions.assertNotNull(executorService2);
    Assertions.assertNotSame(executorService, executorService2);

    builder.setHitDispatcher(null);
    final HitDispatcher hitDispatcher = builder.getOrCreateHitDispatcher();
    Assertions.assertSame(hitDispatcher, builder.getOrCreateHitDispatcher());

    // Build and test the client has the same settings
    GoogleAnalyticsClient ga = builder.build();
    Assertions.assertSame(hitDispatcher, ga.getHitDispatcher());
    Assertions.assertSame(executorService2, ga.getExecutorService());

    // Try setting parameters
    final PartialBuilder<Builder> perHitParameters = builder.getOrCreatePerHitParameters();
    Assertions.assertSame(perHitParameters, builder.getOrCreatePerHitParameters());
    builder.setPerHitParameters(ProtocolVersionParameter.V1);
    Assertions.assertNotSame(perHitParameters, builder.getOrCreatePerHitParameters());
    Assertions.assertEquals("v=1", builder.getOrCreatePerHitParameters().build().format());

    final PartialBuilder<Builder> perSessionParameters = builder.getOrCreatePerSessionParameters();
    Assertions.assertSame(perSessionParameters, builder.getOrCreatePerSessionParameters());
    builder.setPerSessionParameters(SessionControlParameter.START);
    Assertions.assertNotSame(perSessionParameters, builder.getOrCreatePerSessionParameters());
    Assertions.assertEquals("sc=start", builder.getOrCreatePerSessionParameters().build().format());

    // Note: Setting these will make the hit dispatcher null
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

    final long sessionTimeout = builder.getSessionTimeout() + 10;
    builder.setSessionTimeout(sessionTimeout);
    Assertions.assertEquals(sessionTimeout, builder.getSessionTimeout());

    // Hit all edge cases of build by using the string client ID
    builder.setClientId(clientId);
    ga = builder.build();
  }

  @Test
  public void testProperties() {
    final Builder builder = GoogleAnalyticsClient.newBuilder(trackingId);
    final GoogleAnalyticsClient ga = builder.build();

    ga.setIgnore(true);
    Assertions.assertTrue(ga.isIgnore());
    ga.setIgnore(false);
    Assertions.assertFalse(ga.isIgnore());

    Assertions.assertFalse(ga.isShutdown());
    ga.getExecutorService().shutdown();
    Assertions.assertTrue(ga.isShutdown());

    Assertions.assertFalse(ga.isDisabled());
    ga.getHitDispatcher().stop();
    Assertions.assertTrue(ga.isDisabled());

    // Test reset session
    String hit = ga.exception().build().format();
    Assertions.assertTrue(hit.contains("sc=start"));
    hit = ga.exception().build().format();
    Assertions.assertFalse(hit.contains("sc=start"));
    ga.resetSession();
    hit = ga.exception().build().format();
    Assertions.assertTrue(hit.contains("sc=start"));
  }

  @Test
  public void testHits() {
    final Builder builder = GoogleAnalyticsClient.newBuilder(trackingId);
    final GoogleAnalyticsClient ga = builder.build();

    String hit;
    for (final HitType hitType : HitType.values()) {
      hit = ga.hit(hitType).build().format();
      Assertions.assertTrue(hit.contains("t=" + hitType.toString()));
    }

    final String documentLocationUrl = "http://www.abc.com/test";
    hit = ga.pageview(documentLocationUrl).build().format();
    testContains(hit, "t=pageview");
    testContains(hit, "dl=" + UrlEncoderHelper.encode(documentLocationUrl));

    final String documentHostName = "www.abc.com";
    final String documentPath = "/test";
    hit = ga.pageview(documentHostName, documentPath).build().format();
    testContains(hit, "t=pageview");
    testContains(hit, "dh=" + documentHostName);
    testContains(hit, "dp=" + UrlEncoderHelper.encode(documentPath));

    final String screenName = "TestScreen";
    hit = ga.screenview(screenName).build().format();
    testContains(hit, "t=screenview");
    testContains(hit, "cd=" + screenName);

    final String eventCategory = "cat1";
    final String eventAction = "act1";
    hit = ga.event(eventCategory, eventAction).build().format();
    testContains(hit, "t=event");
    testContains(hit, "ec=" + eventCategory);
    testContains(hit, "ea=" + eventAction);

    final String transactionId = "34657-ABC";
    hit = ga.transaction(transactionId).build().format();
    testContains(hit, "t=transaction");
    testContains(hit, "ti=" + transactionId);

    final String itemName = "Item-57";
    hit = ga.item(transactionId, itemName).build().format();
    testContains(hit, "t=item");
    testContains(hit, "ti=" + transactionId);
    testContains(hit, "in=" + itemName);

    final String socialNetwork = "headbook";
    final String socialAction = "like";
    final String socialActionTarget = "friend";
    hit = ga.social(socialNetwork, socialAction, socialActionTarget).build().format();
    testContains(hit, "t=social");
    testContains(hit, "sn=" + socialNetwork);
    testContains(hit, "sa=" + socialAction);
    testContains(hit, "st=" + socialActionTarget);

    hit = ga.exception().build().format();
    testContains(hit, "t=exception");

    final String userTimingCategory = "test-timing";
    final String userTimingVariableName = "testing";
    final int userTimingTime = 76897979;
    hit = ga.timing(userTimingCategory, userTimingVariableName, userTimingTime).build().format();
    testContains(hit, "t=timing");
    testContains(hit, "utc=" + userTimingCategory);
    testContains(hit, "utv=" + userTimingVariableName);
    testContains(hit, "utt=" + userTimingTime);
  }

  private static void testContains(String hit, String sequence) {
    Assertions.assertTrue(hit.contains(sequence),
        () -> String.format("%s missing %s", hit, sequence));
  }

  @Test
  public void testSend() throws InterruptedException, ExecutionException {

    // Create a dummy hit dispatcher that does nothing
    final HitDispatcher hitDispatcher = new HitDispatcher() {

      public boolean disabled;

      @Override
      public boolean stop() {
        disabled = true;
        return true;
      }

      @Override
      public boolean start() {
        disabled = false;
        return true;
      }

      @Override
      public DispatchStatus send(CharSequence hit, long timestamp,
          HttpUrlConnectionCallback callback) {
        // Do not connect. Just return as if it worked.
        return DispatchStatus.COMPLETE;
      }

      @Override
      public boolean isDisabled() {
        return disabled;
      }

      @Override
      public IOException getLastIoException() {
        return null;
      }
    };

    // Create a dummy executor service
    final ExecutorService executorService = new ExecutorService() {

      public boolean shutdown;

      @Override
      public void execute(Runnable command) {
        // Do nothing
      }

      @Override
      public <T> Future<T> submit(Runnable task, T result) {
        return null;
      }

      @Override
      public Future<?> submit(Runnable task) {
        return null;
      }

      @Override
      public <T> Future<T> submit(Callable<T> task) {
        return new Future<T>() {

          @Override
          public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
          }

          @Override
          public boolean isCancelled() {
            return false;
          }

          @Override
          public boolean isDone() {
            return true;
          }

          @Override
          public T get() throws ExecutionException {
            // Run the task
            try {
              return task.call();
            } catch (final Exception ex) {
              throw new ExecutionException(ex);
            }
          }

          @Override
          public T get(long timeout, TimeUnit unit) {
            return null;
          }
        };
      }

      @Override
      public List<Runnable> shutdownNow() {
        return null;
      }

      @Override
      public void shutdown() {
        shutdown = true;
      }

      @Override
      public boolean isTerminated() {
        return false;
      }

      @Override
      public boolean isShutdown() {
        return shutdown;
      }

      @Override
      public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
        return null;
      }

      @Override
      public <T> T invokeAny(Collection<? extends Callable<T>> tasks) {
        return null;
      }

      @Override
      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
          TimeUnit unit) {
        return null;
      }

      @Override
      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        return null;
      }

      @Override
      public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
      }
    };

    final GoogleAnalyticsClient ga = GoogleAnalyticsClient.newBuilder(trackingId)
        .setHitDispatcher(hitDispatcher).setExecutorService(executorService).build();

    Assertions.assertEquals(DispatchStatus.COMPLETE, ga.exception().send().get());

    executorService.shutdown();
    Assertions.assertEquals(DispatchStatus.SHUTDOWN, ga.exception().send().get());

    ga.getHitDispatcher().stop();
    Assertions.assertEquals(DispatchStatus.DISABLED, ga.exception().send().get());

    ga.setIgnore(true);
    Assertions.assertEquals(DispatchStatus.IGNORED, ga.exception().send().get());
  }

  @Test
  public void testSendUsingDebugServer()
      throws InterruptedException, ExecutionException, TimeoutException {

    GoogleAnalyticsClient ga = GoogleAnalyticsClient.newBuilder(trackingId).setDebug(true).build();

    // Submit requests. Use something that should be encoded.
    final String documentHostName = "www.abc.com";
    final String documentPath = "/path/within/application/";
    DispatchStatus status =
        ga.pageview(documentHostName, documentPath).send().get(2000, TimeUnit.MILLISECONDS);
    Assertions.assertEquals(DispatchStatus.COMPLETE, status);

    // Test using an explicit proxy
    ga = GoogleAnalyticsClient.newBuilder(trackingId)
        .setHitDispatcher(DefaultHitDispatcher.getDefault(true, true, Proxy.NO_PROXY)).build();
    status = ga.pageview(documentHostName, documentPath).send().get(2000, TimeUnit.MILLISECONDS);
    Assertions.assertEquals(DispatchStatus.COMPLETE, status);
  }

  @Test
  public void testBuildUsingDebugServer() {

    final HitDispatcher hitDispatcher = DefaultHitDispatcher.getDefault(true, true);
    final GoogleAnalyticsClient ga =
        GoogleAnalyticsClient.newBuilder(trackingId).setHitDispatcher(hitDispatcher).build();

    final DefaultHttpUrlConnectionCallback content = new DefaultHttpUrlConnectionCallback();
    final StringBuilder hit = new StringBuilder();

    // Submit requests. Use something that should be encoded.
    final String documentHostName = "www.abc.com";
    final String documentPath = "/path/within/application/";
    Parameters parameters = ga.pageview(documentHostName, documentPath).build();

    // Single hit
    parameters.formatTo(hit);
    DispatchStatus status = hitDispatcher.send(hit, 0, content);
    debugSend(content);
    Assertions.assertEquals(DispatchStatus.COMPLETE, status);
    Assertions.assertEquals(HttpURLConnection.HTTP_OK, content.getResponseCode());
    Assertions.assertTrue(content.getContentType().contains("utf-8"));
    Assertions.assertTrue(content.getBytesAsText().contains("\"valid\": true"));
    Assertions.assertTrue(content.getBytesAsText().contains("Found 1 hit"));

    // Batch hit. Just repeat the hit.
    hit.append("\n");
    parameters.formatTo(hit);
    status = hitDispatcher.send(hit, 0, content);
    debugSend(content);
    Assertions.assertEquals(DispatchStatus.COMPLETE, status);
    Assertions.assertEquals(HttpURLConnection.HTTP_OK, content.getResponseCode());
    Assertions.assertTrue(content.getContentType().contains("utf-8"));
    Assertions.assertTrue(content.getBytesAsText().contains("\"valid\": true"));
    Assertions.assertTrue(content.getBytesAsText().contains("Found 2 hit"));

    // Bad hit due to missing parameters
    parameters = ga.hit(HitType.PAGEVIEW).build();
    hit.setLength(0);
    parameters.formatTo(hit);
    status = hitDispatcher.send(hit, 0, content);
    debugSend(content);
    Assertions.assertEquals(DispatchStatus.COMPLETE, status);
    Assertions.assertEquals(HttpURLConnection.HTTP_OK, content.getResponseCode());
    Assertions.assertTrue(content.getContentType().contains("utf-8"));
    Assertions.assertTrue(content.getBytesAsText().contains("\"valid\": false"));
    Assertions.assertTrue(content.getBytesAsText().contains("Found 1 hit"));
  }

  private static void debugSend(DefaultHttpUrlConnectionCallback content) {
    logger.log(debugLevel, () -> String.format("Response Code = %d%nResponse =%n%s",
        content.getResponseCode(), content.getBytesAsText()));
  }

  @Test
  public void testDemos() throws InterruptedException {
    final HitDispatcher hitDispatcher = new HitDispatcher() {
      @Override
      public DispatchStatus send(CharSequence hit, long timestamp,
          HttpUrlConnectionCallback callback) {
        logger.log(debugLevel, hit.toString());
        return DispatchStatus.COMPLETE;
      }

      @Override
      public IOException getLastIoException() {
        return null;
      }

      @Override
      public boolean isDisabled() {
        return false;
      }

      @Override
      public boolean start() {
        return true;
      }

      @Override
      public boolean stop() {
        return true;
      }
    };

    final ExecutorService executorService = Executors.newFixedThreadPool(1);

    demo(executorService, hitDispatcher);
    demo2(executorService, hitDispatcher);

    executorService.shutdown();
    executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);

    demo3();
    demo4();
    demo5();
  }

  /////////////////////////////////////////////////////////////////////////////////
  // Demo of using the tracker. This code is placed in the project README.md file.
  /////////////////////////////////////////////////////////////////////////////////

  //@formatter:off
  private static void demo(ExecutorService executorService, HitDispatcher hitDispatcher) {
    // Create the tracker
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String userId = "Anything";

    final GoogleAnalyticsClient ga =
        GoogleAnalyticsClient.newBuilder(trackingId)
                             .setUserId(userId)
                             .setExecutorService(executorService)
                             .setHitDispatcher(hitDispatcher)
                             .build();

    // Submit requests
    final String documentHostName = "www.abc.com";
    final String documentPath = "/path/within/application/";
    ga.pageview(documentHostName, documentPath).send();
  }

  private static void demo2(ExecutorService executorService, HitDispatcher hitDispatcher) {
    // Create the tracker
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String userId = "Anything";
    final String documentHostName = "www.abc.com";

    final GoogleAnalyticsClient ga =
        GoogleAnalyticsClient.newBuilder(trackingId)
                             .setUserId(userId)
                             .getOrCreatePerHitParameters()
                                 .addDocumentHostName(documentHostName)
                                 .getParent()
                             .setExecutorService(executorService)
                             .setHitDispatcher(hitDispatcher)
                             .build();

    // Submit requests
    final String documentPath = "/path/within/application/";
    ga.hit(HitType.PAGEVIEW).addDocumentPath(documentPath).send();
  }

  private static void demo3() {
    // Create the tracker
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String hit = Parameters.newRequiredBuilder(trackingId)
        .addHitType(HitType.EXCEPTION)
        .addExceptionDescription("Something went wrong")
        .build()
        .format();
    logger.log(debugLevel, hit);
  }

  private static void demo4() {
    // Create the tracker
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String hit = Parameters.newRequiredBuilder(trackingId)
        .addHitType(HitType.ITEM)
        .add(new NoIndexTextParameter(ProtocolSpecification.TRANSACTION_ID, "Trans.1"))
        .add(new NoIndexTextParameter(ProtocolSpecification.ITEM_NAME, "Item.2"))
        .add(new OneIndexTextParameter(ProtocolSpecification.PRODUCT_SKU, 23, "SKU.4567"))
        .build()
        .format();
    logger.log(debugLevel, hit);
  }

  private static void demo5() {
    // Generic name=value pair
    final String name = "anything";
    final String value = "some text";

    // Custom indexed parameter
    final String formalName = "My parameter";
    final String nameFormat = "myp_"; // Underscore for the index
    final ValueType valueType = ValueType.INTEGER;
    final int maxLength = 0;
    final ParameterSpecification specification = new CustomParameterSpecification(
        formalName, nameFormat, valueType, maxLength);
    final int index = 44;
    final int value2 = 123;

    final String hit = Parameters.newBuilder()
        .add(name, value)
        .add(new OneIndexIntParameter(specification, index, value2))
        .build()
        .format();
    logger.log(debugLevel, hit);
  }
}
