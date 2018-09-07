/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package uk.ac.sussex.gdsc.analytics.parameters;

import uk.ac.sussex.gdsc.analytics.TestUtils;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.HitBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.PartialBuilder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

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
    // @formatter:on
  }

  @Test
  public void testPartialBuilder() throws MalformedURLException {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String applicationName = TestUtils.randomName(rg, 3);
      String documentTitle = TestUtils.randomName(rg, 3);
      // @formatter:off
      PartialBuilder<?> builder = Parameters.newPartialBuilder(this); 
      Assertions.assertThat(this).isSameAs(builder.getParent());
      URL url = createURL(builder.addApplicationName(applicationName)
                                 .addDocumentTitle(documentTitle)
                                 .build());
      Assertions.assertThat(url)
      .hasParameter("an", applicationName)
      .hasParameter("dt", documentTitle)
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
