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

package uk.ac.sussex.gdsc.analytics.parameters;

import uk.ac.sussex.gdsc.analytics.TestUtils;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.Builder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.HitBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.PartialBuilder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ParametersTest {

  String trackingId = "UA-1234-5";
  String clientId = "123e4567-e89b-12d3-a456-426655440000";
  String clientId2 = "00112233-4455-6677-8899-aabbccddeeff";
  String userId = "Mr. Test";

  @Test
  public void testRequiredBuilder() throws MalformedURLException {
    // @formatter:off
    URL url = createURL(Parameters.newRequiredBuilder(trackingId)
                                  .build());
    Assertions.assertThat(url)
    .hasParameter("v", "1")
    .hasParameter("je", "1")
    .hasParameter("tid", trackingId)
    .hasParameter("cid") // This will be a random UUID
    .hasNoParameter("uid")
    ;

    // Check it is a random UUID
    UUID.fromString(getParameter(url.getQuery(), "cid"));

    Assertions.assertThat(
        createURL(Parameters.newRequiredBuilder(trackingId)
                            .addClientId(clientId).build()))
    .hasParameter("v", "1")
    .hasParameter("je", "1")
    .hasParameter("tid", trackingId)
    .hasParameter("cid", clientId)
    .hasNoParameter("uid")
    ;

    UUID uuid = UUID.fromString(clientId2);
    Assertions.assertThat(
        createURL(Parameters.newRequiredBuilder(trackingId)
                            .addClientId(uuid).build()))
    .hasParameter("v", "1")
    .hasParameter("je", "1")
    .hasParameter("tid", trackingId)
    .hasParameter("cid", clientId2)
    .hasNoParameter("uid")
    ;

    Assertions.assertThat(
        createURL(Parameters.newRequiredBuilder(trackingId)
                            .addUserId(userId).build()))
    .hasParameter("v", "1")
    .hasParameter("je", "1")
    .hasParameter("tid", trackingId)
    .hasParameter("uid", userId)
    .hasNoParameter("cid")
    ;


    Assertions.assertThat(
        createURL(Parameters.newRequiredBuilder(trackingId)
                            .addUserId(userId)
                            .addClientId(clientId).build()))
    .hasParameter("v", "1")
    .hasParameter("je", "1")
    .hasParameter("tid", trackingId)
    .hasParameter("uid", userId)
    .hasParameter("cid", clientId)
    ;
    // @formatter:on
  }

  @Test
  public void testRequiredBuilderThrows() {
    // @formatter:off
    // Bad tracking Id
    Assertions.assertThatThrownBy(() -> Parameters
        .newRequiredBuilder("ashdjk"))
        .isInstanceOf(IllegalArgumentException.class);

    // Duplicate client id
    Assertions
        .assertThatThrownBy(() -> Parameters
            .newRequiredBuilder(trackingId)
            .addClientId(clientId)
            .addClientId(clientId))
        .isInstanceOf(IllegalArgumentException.class);

    // Duplicate client id
    UUID uuid = UUID.fromString(clientId2);
    Assertions.assertThatThrownBy(() -> Parameters
        .newRequiredBuilder(trackingId)
        .addClientId(uuid)
        .addClientId(uuid))
        .isInstanceOf(IllegalArgumentException.class);

    // Duplicate user id
    Assertions.assertThatThrownBy(() -> Parameters
        .newRequiredBuilder(trackingId)
        .addUserId(userId)
        .addUserId(userId))
        .isInstanceOf(IllegalArgumentException.class);

    // Require '/' on document path
    Assertions.assertThatThrownBy(() -> Parameters
        .newRequiredBuilder(trackingId)
        .addUserId(userId)
        .addDocumentPath("path/with/no/leading/slash"))
        .isInstanceOf(IllegalArgumentException.class);
    // @formatter:on
  }

  @Test
  public void testPartialBuilder() throws MalformedURLException {
    // Test empty methods
    // @formatter:off
    String emptyHit =
    Parameters.newPartialBuilder(this)
              .addTrackingId(trackingId)
              .addVersion()
              .addClientId(clientId)
              .addClientId(UUID.fromString(clientId))
              .addUserId(userId)
              .build().format();
    // @formatter:on
    Assertions.assertThat(emptyHit).isEmpty();

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      // Test can add others
      String applicationName = TestUtils.randomName(rg, 3);
      String documentTitle = TestUtils.randomName(rg, 3);
      // @formatter:off
      PartialBuilder<?> builder = Parameters.newPartialBuilder(this);
      // Test getParent()
      Assertions.assertThat(this).isSameAs(builder.getParent());
      URL url = createURL(builder.addApplicationName(applicationName)
                                 .addDocumentTitle(documentTitle)
                                 // These should be ignored
                                 .addVersion()
                                 .addTrackingId(trackingId)
                                 .addClientId(clientId)
                                 .addClientId(UUID.fromString(clientId))
                                 .addUserId(userId)
                                 .build());
      Assertions.assertThat(url)
      .hasParameter("an", applicationName)
      .hasParameter("dt", documentTitle)
      .hasNoParameter("v")
      .hasNoParameter("tid")
      .hasNoParameter("cid")
      .hasNoParameter("uid")
      ;
      // @formatter:on
    }
  }

  @Test
  public void testBuilder() throws MalformedURLException {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String applicationName = TestUtils.randomName(rg, 3);
      String documentTitle = TestUtils.randomName(rg, 3);
      String userId = TestUtils.randomName(rg, 3);
      // @formatter:off
      URL url = createURL(Parameters.newBuilder()
                                    .addApplicationName(applicationName)
                                    .addDocumentTitle(documentTitle)
                                    .addUserId(userId)
                                    .build());
      Assertions.assertThat(url)
      .hasParameter("an", applicationName)
      .hasParameter("dt", documentTitle)
      .hasParameter("uid", userId)
      ;
      // @formatter:on
    }
  }

  @Test
  public void testBuilderApi() throws MalformedURLException {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 3; i++) {
      // General
      testApi((t) -> t.addVersion(), "v", "1");
      String trackingId = "UA-0000-" + i;
      testApi((t) -> t.addTrackingId(trackingId), "tid", trackingId);
      boolean anonymizeIp = rg.nextBoolean();
      testApi((t) -> t.addAnonymizeIp(anonymizeIp), "aip", (anonymizeIp) ? "1" : "0");
      String dataSource = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDataSource(dataSource), "ds", dataSource);
      testApi((t) -> t.addQueueTime(System.currentTimeMillis()), "qt", null);
      testApi((t) -> t.addQueueTime(), "qt", null);
      testApi((t) -> t.addCacheBuster(), "z", null);

      // User
      String clientId = "00112233-4455-6677-8899-aabbccddeef" + Integer.toString(rg.nextInt(10));
      testApi((t) -> t.addClientId(clientId), "cid", clientId);
      testApi((t) -> t.addClientId(UUID.fromString(clientId)), "cid", clientId);
      String userId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserId(userId), "uid", userId);

      // Session
      boolean start = rg.nextBoolean();
      SessionControl sessionControl = (start) ? SessionControl.START : SessionControl.END;
      testApi((t) -> t.addSessionControl(sessionControl), "sc", (start) ? "start" : "end");

      // System Info
      int width = rg.nextInt(2048);
      int height = rg.nextInt(1440);
      String resolution = String.format("%dx%d", width, height);
      testApi((t) -> t.addScreenResolution(width, height), "sr", resolution);
      testApi((t) -> t.addViewportSize(width, height), "vp", resolution);
      String documentEncoding = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDocumentEncoding(documentEncoding), "de", documentEncoding);
      String screenColorDepth = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addScreenColors(screenColorDepth), "sd", screenColorDepth);
      Locale locale = Locale.FRENCH;
      testApi((t) -> t.addUserLanguage(locale), "ul", locale.toLanguageTag());
      testApi((t) -> t.addUserLanguage(), "ul", Locale.getDefault().toLanguageTag());
      boolean javaEnabled = rg.nextBoolean();
      testApi((t) -> t.addJavaEnabled(javaEnabled), "je", (javaEnabled) ? "1" : "0");
      String flashVersion = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addFlashVersion(flashVersion), "fl", flashVersion);

      // Hit
      HitType hitType = HitType.values()[rg.nextInt(HitType.values().length)];
      testApi((t) -> t.addHitType(hitType), "t", hitType.toString());
      boolean nonInteractive = rg.nextBoolean();
      testApi((t) -> t.addNonInteractionHit(nonInteractive), "ni", (nonInteractive) ? "1" : "0");

      // Content Information
      String documentHostName = "www." + TestUtils.randomName(rg, 3) + ".com";
      String documentPath = TestUtils.randomPath(rg, 10);
      String documentLocationUrl = "http://www.abc.com/" + documentPath;
      testApi((t) -> t.addDocumentLocationUrl(documentLocationUrl), "dl", documentLocationUrl);
      testApi((t) -> t.addDocumentHostName(documentHostName), "dh", documentHostName);
      testApi((t) -> t.addDocumentPath(documentPath), "dp", documentPath);
      String documentTitle = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDocumentTitle(documentTitle), "dt", documentTitle);
      String screenName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addScreenName(screenName), "cd", screenName);
      int groupIndex = 1 + rg.nextInt(200);
      String contentGroup = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addContentGroup(groupIndex, contentGroup), "cg" + groupIndex, contentGroup);
      String linkId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addLinkeId(linkId), "linkid", linkId);

      // App Tracking
      String applicationName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationName(applicationName), "an", applicationName);
      String applicationId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationId(applicationId), "aid", applicationId);
      String applicationVersion = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationVersion(applicationVersion), "av", applicationVersion);
      String applicationInstallerId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationInstallerId(applicationInstallerId), "aiid",
          applicationInstallerId);

      // Event Tracking
      String eventCategory = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventCategory(eventCategory), "ec", eventCategory);
      String eventAction = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventAction(eventAction), "ea", eventAction);
      String eventLabel = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventLabel(eventLabel), "el", eventLabel);
      int eventValue = rg.nextInt(1000);
      testApi((t) -> t.addEventValue(eventValue), "ev", Integer.toString(eventValue));

      // Timing
      // testApi((t) -> t.add, "", );
      String userTimingCategory = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingCategory(userTimingCategory), "utc", userTimingCategory);
      String userTimingVariableName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingVariableName(userTimingVariableName), "utv",
          userTimingVariableName);
      int userTimingTime = rg.nextInt(1000);
      testApi((t) -> t.addUserTimingTime(userTimingTime), "utt", Integer.toString(userTimingTime));
      String userTimingLabel = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingLabel(userTimingLabel), "utl", userTimingLabel);
      int pageLoadTime = rg.nextInt(1000);
      testApi((t) -> t.addPageLoadTime(pageLoadTime), "plt", Integer.toString(pageLoadTime));
      int dnsTime = rg.nextInt(1000);
      testApi((t) -> t.addDnsTime(dnsTime), "dns", Integer.toString(dnsTime));
      int pageDownloadTime = rg.nextInt(1000);
      testApi((t) -> t.addPageDownloadTime(pageDownloadTime), "pdt",
          Integer.toString(pageDownloadTime));
      int redirectResponseTime = rg.nextInt(1000);
      testApi((t) -> t.addRedirectResponseTime(redirectResponseTime), "rrt",
          Integer.toString(redirectResponseTime));
      int tcpConnectTime = rg.nextInt(1000);
      testApi((t) -> t.addTcpConnectTime(tcpConnectTime), "tcp", Integer.toString(tcpConnectTime));
      int serverResponseTime = rg.nextInt(1000);
      testApi((t) -> t.addServerResponseTime(serverResponseTime), "srt",
          Integer.toString(serverResponseTime));
      int domInteractiveTime = rg.nextInt(1000);
      testApi((t) -> t.addDomInteractiveTime(domInteractiveTime), "dit",
          Integer.toString(domInteractiveTime));
      int contentLoadTime = rg.nextInt(1000);
      testApi((t) -> t.addContentLoadTime(contentLoadTime), "clt",
          Integer.toString(contentLoadTime));

      // Exceptions
      String exceptionDescription = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addExceptionDescription(exceptionDescription), "exd", exceptionDescription);
      boolean exceptionFatal = rg.nextBoolean();
      testApi((t) -> t.addIsExceptionFatal(exceptionFatal), "exf", (exceptionFatal) ? "1" : "0");

      // Custom Dimensions/Metrics
      int dimIndex = 1 + rg.nextInt(200);
      String dimValue = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addCustomDimension(dimIndex, dimValue), "cd" + dimIndex, dimValue);
      int metIndex = 1 + rg.nextInt(200);
      int metValue = rg.nextInt(1000);
      testApi((t) -> t.addCustomMetric(metIndex, metValue), "cm" + metIndex,
          Integer.toString(metValue));

      // Custom
      String name = TestUtils.randomName(rg, 3);
      String value = TestUtils.randomName(rg, 3);
      testApi((t) -> t.add(name, value), name, value);
    }
  }

  private static void testApi(Consumer<Builder> fun, String name, String value)
      throws MalformedURLException {
    Builder builder = Parameters.newBuilder();
    fun.accept(builder);
    URL url = createURL(builder.build());
    if (value != null) {
      Assertions.assertThat(url).hasParameter(name, value);
    } else {
      Assertions.assertThat(url).hasParameter(name);
    }
  }

  @Test
  public void testHitBuilder() {

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      long currentTime = System.currentTimeMillis() - rg.nextInt(1000);
      HitBuilder<URL> builder = new HitBuilder<URL>(null, HitTypeParameter.PAGEVIEW, currentTime) {
        @Override
        public URL send() {
          try {
            return createURL(build());
          } catch (MalformedURLException ex) {
            Assertions.fail(ex.getMessage());
            return null; // For the Java compiler
          }
        }
      };
      Assertions.assertThat(builder.getTimestamp()).isEqualTo(currentTime);
      String documentPath = TestUtils.randomPath(rg, 10);
      String documentTitle = TestUtils.randomName(rg, 3);
      // @formatter:off
      URL url = builder.addDocumentPath(documentPath)
                       .addDocumentTitle(documentTitle)
                       .send();
      Assertions.assertThat(url)
      .hasParameter("t", "pageview")
      .hasParameter("dp", documentPath)
      .hasParameter("dt", documentTitle)
      ;
      // @formatter:on
    }
  }

  // Add a test for all the supported parameters from each section in the Google reference.



  /**
   * Gets the parameter from the query string
   *
   * @param query the query
   * @param string the string
   * @return the parameter (or null)
   */
  private static String getParameter(String query, String string) {
    int i = query.indexOf(string + "=");
    if (i == -1) {
      return null;
    }
    i += string.length() + 1;
    int i2 = query.indexOf('&', i + string.length());
    String param = (i2 == -1) ? query.substring(i) : query.substring(i, i2);
    try {
      return URLDecoder.decode(param, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      Assertions.fail(ex.getMessage());
      return null; // For the Java compiler
    }
  }

  /**
   * Creates the URL using a dummy hostname and adding the formatted string for a GET request.
   *
   * @param parameters the parameters
   * @return the url
   * @throws MalformedURLException the malformed URL exception
   */
  private static URL createURL(Parameters parameters) throws MalformedURLException {
    return new URL("http://www.abc.com?" + parameters.format());
  }
}
