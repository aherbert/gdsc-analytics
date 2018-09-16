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
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.assertj.core.api.AbstractUrlAssert;
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
    final URL url = createURL(Parameters.newRequiredBuilder(trackingId)
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

    final UUID uuid = UUID.fromString(clientId2);
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
    final UUID uuid = UUID.fromString(clientId2);
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
    final String emptyHit =
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
      final String applicationName = TestUtils.randomName(rg, 3);
      final String documentTitle = TestUtils.randomName(rg, 3);
      // @formatter:off
      final PartialBuilder<?> builder = Parameters.newPartialBuilder(this);
      // Test getParent()
      Assertions.assertThat(this).isSameAs(builder.getParent());
      final URL url = createURL(builder.addApplicationName(applicationName)
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
      final String applicationName = TestUtils.randomName(rg, 3);
      final String documentTitle = TestUtils.randomName(rg, 3);
      final String userId = TestUtils.randomName(rg, 3);
      // @formatter:off
      final URL url = createURL(Parameters.newBuilder()
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
      final String trackingId = "UA-0000-" + i;
      testApi((t) -> t.addTrackingId(trackingId), "tid", trackingId);
      final boolean anonymizeIp = rg.nextBoolean();
      testApi((t) -> t.addAnonymizeIp(anonymizeIp), "aip", (anonymizeIp) ? "1" : "0");
      final String dataSource = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDataSource(dataSource), "ds", dataSource);
      testApi((t) -> t.addQueueTime(System.currentTimeMillis()), "qt", null);
      testApi((t) -> t.addQueueTime(), "qt", null);
      testApi((t) -> t.addCacheBuster(), "z", null);

      // User
      final String clientId =
          "00112233-4455-6677-8899-aabbccddeef" + Integer.toString(rg.nextInt(10));
      testApi((t) -> t.addClientId(clientId), "cid", clientId);
      testApi((t) -> t.addClientId(UUID.fromString(clientId)), "cid", clientId);
      final String userId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserId(userId), "uid", userId);

      // Session
      final boolean start = rg.nextBoolean();
      final SessionControl sessionControl = (start) ? SessionControl.START : SessionControl.END;
      testApi((t) -> t.addSessionControl(sessionControl), "sc", (start) ? "start" : "end");

      // System Info
      final int width = rg.nextInt(2048);
      final int height = rg.nextInt(1440);
      final String resolution = String.format("%dx%d", width, height);
      testApi((t) -> t.addScreenResolution(width, height), "sr", resolution);
      testApi((t) -> t.addViewportSize(width, height), "vp", resolution);
      final String documentEncoding = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDocumentEncoding(documentEncoding), "de", documentEncoding);
      final String screenColorDepth = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addScreenColors(screenColorDepth), "sd", screenColorDepth);
      final Locale locale = Locale.FRENCH;
      testApi((t) -> t.addUserLanguage(locale), "ul", locale.toLanguageTag());
      testApi((t) -> t.addUserLanguage(), "ul", Locale.getDefault().toLanguageTag());
      final boolean javaEnabled = rg.nextBoolean();
      testApi((t) -> t.addJavaEnabled(javaEnabled), "je", (javaEnabled) ? "1" : "0");
      final String flashVersion = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addFlashVersion(flashVersion), "fl", flashVersion);

      // Hit
      final HitType hitType = HitType.values()[rg.nextInt(HitType.values().length)];
      testApi((t) -> t.addHitType(hitType), "t", hitType.toString());
      final boolean nonInteractive = rg.nextBoolean();
      testApi((t) -> t.addNonInteractionHit(nonInteractive), "ni", (nonInteractive) ? "1" : "0");

      // Content Information
      final String documentHostName = "www." + TestUtils.randomName(rg, 3) + ".com";
      final String documentPath = TestUtils.randomPath(rg, 10);
      final String documentLocationUrl = "http://www.abc.com/" + documentPath;
      testApi((t) -> t.addDocumentLocationUrl(documentLocationUrl), "dl", documentLocationUrl);
      testApi((t) -> t.addDocumentHostName(documentHostName), "dh", documentHostName);
      testApi((t) -> t.addDocumentPath(documentPath), "dp", documentPath);
      final String documentTitle = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addDocumentTitle(documentTitle), "dt", documentTitle);
      final String screenName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addScreenName(screenName), "cd", screenName);
      final int groupIndex = 1 + rg.nextInt(200);
      final String contentGroup = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addContentGroup(groupIndex, contentGroup), "cg" + groupIndex, contentGroup);
      final String linkId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addLinkeId(linkId), "linkid", linkId);

      // App Tracking
      final String applicationName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationName(applicationName), "an", applicationName);
      final String applicationId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationId(applicationId), "aid", applicationId);
      final String applicationVersion = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationVersion(applicationVersion), "av", applicationVersion);
      final String applicationInstallerId = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addApplicationInstallerId(applicationInstallerId), "aiid",
          applicationInstallerId);

      // Event Tracking
      final String eventCategory = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventCategory(eventCategory), "ec", eventCategory);
      final String eventAction = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventAction(eventAction), "ea", eventAction);
      final String eventLabel = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addEventLabel(eventLabel), "el", eventLabel);
      final int eventValue = rg.nextInt(1000);
      testApi((t) -> t.addEventValue(eventValue), "ev", Integer.toString(eventValue));

      // Timing
      // testApi((t) -> t.add, "", );
      final String userTimingCategory = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingCategory(userTimingCategory), "utc", userTimingCategory);
      final String userTimingVariableName = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingVariableName(userTimingVariableName), "utv",
          userTimingVariableName);
      final int userTimingTime = rg.nextInt(1000);
      testApi((t) -> t.addUserTimingTime(userTimingTime), "utt", Integer.toString(userTimingTime));
      final String userTimingLabel = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addUserTimingLabel(userTimingLabel), "utl", userTimingLabel);
      final int pageLoadTime = rg.nextInt(1000);
      testApi((t) -> t.addPageLoadTime(pageLoadTime), "plt", Integer.toString(pageLoadTime));
      final int dnsTime = rg.nextInt(1000);
      testApi((t) -> t.addDnsTime(dnsTime), "dns", Integer.toString(dnsTime));
      final int pageDownloadTime = rg.nextInt(1000);
      testApi((t) -> t.addPageDownloadTime(pageDownloadTime), "pdt",
          Integer.toString(pageDownloadTime));
      final int redirectResponseTime = rg.nextInt(1000);
      testApi((t) -> t.addRedirectResponseTime(redirectResponseTime), "rrt",
          Integer.toString(redirectResponseTime));
      final int tcpConnectTime = rg.nextInt(1000);
      testApi((t) -> t.addTcpConnectTime(tcpConnectTime), "tcp", Integer.toString(tcpConnectTime));
      final int serverResponseTime = rg.nextInt(1000);
      testApi((t) -> t.addServerResponseTime(serverResponseTime), "srt",
          Integer.toString(serverResponseTime));
      final int domInteractiveTime = rg.nextInt(1000);
      testApi((t) -> t.addDomInteractiveTime(domInteractiveTime), "dit",
          Integer.toString(domInteractiveTime));
      final int contentLoadTime = rg.nextInt(1000);
      testApi((t) -> t.addContentLoadTime(contentLoadTime), "clt",
          Integer.toString(contentLoadTime));

      // Exceptions
      final String exceptionDescription = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addExceptionDescription(exceptionDescription), "exd", exceptionDescription);
      final boolean exceptionFatal = rg.nextBoolean();
      testApi((t) -> t.addIsExceptionFatal(exceptionFatal), "exf", (exceptionFatal) ? "1" : "0");

      // Custom Dimensions/Metrics
      final int dimIndex = 1 + rg.nextInt(200);
      final String dimValue = TestUtils.randomName(rg, 3);
      testApi((t) -> t.addCustomDimension(dimIndex, dimValue), "cd" + dimIndex, dimValue);
      final int metIndex = 1 + rg.nextInt(200);
      final int metValue = rg.nextInt(1000);
      testApi((t) -> t.addCustomMetric(metIndex, metValue), "cm" + metIndex,
          Integer.toString(metValue));

      // Custom
      final String name = TestUtils.randomName(rg, 3);
      final String value = TestUtils.randomName(rg, 3);
      testApi((t) -> t.add(name, value), name, value);
    }
  }

  private static void testApi(Consumer<Builder> fun, String name, String value)
      throws MalformedURLException {
    final Builder builder = Parameters.newBuilder();
    fun.accept(builder);
    final URL url = createURL(builder.build());
    if (value != null) {
      Assertions.assertThat(url).hasParameter(name, value);
    } else {
      Assertions.assertThat(url).hasParameter(name);
    }
  }

  @Test
  public void testBuilderWithManyParameters() throws MalformedURLException {
    final Builder builder = Parameters.newBuilder();
    final int size = 100;
    ArrayList<String> names = new ArrayList<>(size);
    ArrayList<String> values = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      final String name = "name" + i;
      final String value = "value" + i;
      builder.add(name, value);
      names.add(name);
      values.add(value);
    }
    final URL url = createURL(builder.build());
    AbstractUrlAssert<?> a = Assertions.assertThat(url);
    for (int i = 0; i < size; i++) {
      a.hasParameter(names.get(i), values.get(i));
    }
  }

  @Test
  public void testHitBuilder() {

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final long currentTime = System.currentTimeMillis() - rg.nextInt(1000);
      final HitBuilder<URL> builder =
          new HitBuilder<URL>(null, HitTypeParameter.PAGEVIEW, currentTime) {
            @Override
            public URL send() {
              try {
                return createURL(build());
              } catch (final MalformedURLException ex) {
                Assertions.fail(ex.getMessage());
                return null; // For the Java compiler
              }
            }
          };
      Assertions.assertThat(builder.getTimestamp()).isEqualTo(currentTime);
      final String documentPath = TestUtils.randomPath(rg, 10);
      final String documentTitle = TestUtils.randomName(rg, 3);
      // @formatter:off
      final URL url = builder.addDocumentPath(documentPath)
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
    final int i2 = query.indexOf('&', i + string.length());
    final String param = (i2 == -1) ? query.substring(i) : query.substring(i, i2);
    try {
      return URLDecoder.decode(param, "UTF-8");
    } catch (final UnsupportedEncodingException ex) {
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
