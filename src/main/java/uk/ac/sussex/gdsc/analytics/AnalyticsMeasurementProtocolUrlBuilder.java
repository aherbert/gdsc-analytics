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

package uk.ac.sussex.gdsc.analytics;

import uk.ac.sussex.gdsc.analytics.Parameters.CustomDimension;
import uk.ac.sussex.gdsc.analytics.Parameters.CustomMetric;

import java.util.List;
import java.util.Random;

/**
 * Build the parameters used by Google Analytics Measurement Protocol.
 *
 * <p>This class only supports the pageview and event hit type.
 *
 * @see "https://developers.google.com/analytics/devguides/collection/protocol/v1/"
 */
public class AnalyticsMeasurementProtocolUrlBuilder
    implements IAnalyticsMeasurementProtocolUrlBuilder {
  /** The random used for the get URL cache buster. */
  private Random random = null;

  /*
   * (non-Javadoc)
   *
   * @see uk.ac.sussex.gdsc.analytics.IAnalyticsMeasurementProtocolUrlBuilder# getVersion()
   */
  @Override
  public String getVersion() {
    return "1";
  }

  /*
   * (non-Javadoc)
   *
   * @see gdsc.core.analytics.IAnalyticsMeasurementProtocolUrlBuilder#buildUrl(gdsc.
   * core.analytics.ClientParameters, gdsc.core.analytics.RequestParameters, long)
   */
  @Override
  public String buildUrl(ClientParameters clientParameters, RequestParameters requestParameters,
      long timestamp) {
    // Details of how to build a URL are given here:
    // https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#commonhits
    // https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters

    final StringBuilder sb = new StringBuilder();

    sb.append("v=1"); // version

    // Flag to state that session level custom dimensions and metrics should be
    // built
    boolean buildSessionLevelCustomParams = false;

    // Check if this is a new session
    if (clientParameters.isNewSession()) {
      sb.append("&sc=start");
      buildSessionLevelCustomParams = true;
    }

    // Build the client data.
    // Note it is unclear if this can be sent only once at the session level or
    // it should be sent with each hit (which is more expensive). At the moment
    // just send it with every hit.
    String url = clientParameters.getUrl();
    if (url == null) {
      url = buildClientUrl(clientParameters);
      buildSessionLevelCustomParams = true;
    }
    sb.append(url);

    // Add the client custom dimensions and metrics at the session level, so we only
    // build this when a new session or when the client parameters have changed.
    if (buildSessionLevelCustomParams) {
      buildCustomDimensionsUrl(sb, clientParameters.getCustomDimensions());
      buildCustomMetricsUrl(sb, clientParameters.getCustomMetrics());
    }

    // Build the request data

    // Hit type
    add(sb, "t", requestParameters.getHitType());

    // Check for more custom dimensions
    buildCustomDimensionsUrl(sb, requestParameters.getCustomDimensions());
    buildCustomMetricsUrl(sb, requestParameters.getCustomMetrics());

    switch (requestParameters.getHitTypeEnum()) {
      case PAGEVIEW:
        add(sb, "dp", requestParameters.getDocumentPath());
        add(sb, "dt", requestParameters.getDocumentTitle());
        break;

      case EVENT:
        add(sb, "ec", requestParameters.getCategory());
        add(sb, "ea", requestParameters.getAction());
        addCheck(sb, "el", requestParameters.getLabel());
        addCheck(sb, "ev", requestParameters.getValue());
        break;

      default:
        throw new IllegalArgumentException(
            "Unsupported hit type: " + requestParameters.getHitType());
    }

    // Queue time
    // Used to collect offline / latent hits. The value represents the time delta
    // (in milliseconds)
    // between when the hit being reported occurred and the time the hit was sent.
    // The value must be
    // greater than or equal to 0. Values greater than four hours may lead to hits
    // not being processed.
    add(sb, "qt", System.currentTimeMillis() - timestamp);

    return sb.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see gdsc.core.analytics.IAnalyticsMeasurementProtocolURLBuilder#buildGetUrl(gdsc.
   * core.analytics.ClientParameters, gdsc.core.analytics.RequestParameters, long)
   */
  @Override
  public String buildGetUrl(ClientParameters clientParameters, RequestParameters requestParameters,
      long timestamp) {
    // Cache buster.
    // Used to send a random number in GET requests to ensure browsers and proxies
    // don't cache hits.
    // It should be sent as the final parameter of the request
    // https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#z
    Random random = this.random;
    if (random == null) {
      random = new Random();
      this.random = random;
    }
    return buildUrl(clientParameters, requestParameters, timestamp) + "&z=" + random.nextInt();
  }

  /**
   * Add the key value pair.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  private static void add(StringBuilder sb, String key, String value) {
    sb.append('&').append(key).append('=').append(FastUrlEncoder.encode(value));
  }

  /**
   * Add the key value pair.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  private static void add(StringBuilder sb, String key, int value) {
    sb.append('&').append(key).append('=').append(value);
  }

  /**
   * Add the key value pair.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  private static void add(StringBuilder sb, String key, long value) {
    sb.append('&').append(key).append('=').append(value);
  }

  /**
   * Add a custom dimension.
   *
   * @param sb the string builder
   * @param index the index
   * @param value the value
   */
  private static void addDimension(StringBuilder sb, int index, String value) {
    sb.append("&cd").append(index).append('=').append(FastUrlEncoder.encode(value));
  }

  /**
   * Add a custom metric.
   *
   * @param sb the string builder
   * @param index the index
   * @param value the value
   */
  private static void addMetric(StringBuilder sb, int index, int value) {
    sb.append("&cm").append(index).append('=').append(value);
  }

  /**
   * Add the key value pair if the value is not null.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  private static void addCheck(StringBuilder sb, String key, String value) {
    if (value == null) {
      return;
    }
    add(sb, key, value);
  }

  /**
   * Add the key value pair if the value is not null.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  private static void addCheck(StringBuilder sb, String key, Integer value) {
    if (value == null) {
      return;
    }
    add(sb, key, value.intValue());
  }

  private static String buildClientUrl(ClientParameters client) {
    final StringBuilder sb = new StringBuilder();

    add(sb, "tid", client.getTrackingId());
    add(sb, "cid", client.getClientId());
    add(sb, "an", client.getApplicationName());
    addCheck(sb, "aid", client.getApplicationId());
    addCheck(sb, "av", client.getApplicationVersion());

    addCheck(sb, "sr", client.getScreenResolution());
    addCheck(sb, "ul", client.getUserLanguage());
    addCheck(sb, "ua", client.getUserAgent());

    if (client.isAnonymised()) {
      sb.append("&aip=1"); // Anonymise IP
      // sb.append("&dh=localhost");
    } else {
      addCheck(sb, "dh", client.getHostName());
    }

    sb.append("&je=1"); // java enabled

    final String url = sb.toString();
    client.setUrl(url);
    return url;
  }

  private static void buildCustomDimensionsUrl(StringBuilder sb,
      List<CustomDimension> customDimensions) {
    if (customDimensions == null) {
      return;
    }
    for (final CustomDimension cd : customDimensions) {
      addDimension(sb, cd.index, cd.value);
    }
  }

  private static void buildCustomMetricsUrl(StringBuilder sb, List<CustomMetric> customMetrics) {
    if (customMetrics == null) {
      return;
    }
    for (final CustomMetric cm : customMetrics) {
      addMetric(sb, cm.index, cm.value);
    }
  }
}
