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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Contains utilities for Google Analytics.
 */
public final class GoogleAnalyticsUtils {

  /** The logger. */
  private static final Logger logger = Logger.getLogger(GoogleAnalyticsUtils.class.getName());

  /** The protocol for HTTPS. */
  private static final String HTTPS = "https";
  /** The protocol for HTTP. */
  private static final String HTTP = "http";

  /**
   * The system property constant for the Google Analytics hostname component of the URL.
   * 
   * <p>If this is not set then the value defaults to {@link #DEFAULT_GOOGLE_ANALYTICS_HOSTNAME}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_HOSTNAME = "gdsc.analytics.hostname";
  /**
   * The system property constant for the Google Analytics file component of the URL.
   * 
   * <p>If this is not set then the value defaults to {@link #DEFAULT_GOOGLE_ANALYTICS_FILE}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_FILE = "gdsc.analytics.file";
  /**
   * The system property constant for the Google Analytics file component of the URL for the debug
   * server.
   * 
   * <p>If this is not set then the value defaults to {@link #DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_DEBUG_FILE = "gdsc.analytics.debug.file";

  /** The hostname for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_HOSTNAME = "www.google-analytics.com";
  /** The default file for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_FILE = "/collect";
  /** The default debug file for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE = "/debug/collect";

  /** The hostname for the Google Analytics URL. */
  private static String hostname;
  /** The file for the Google Analytics URL. */
  private static String file;
  /** The debug file for the Google Analytics URL. */
  private static String debugFile;

  static {
    refreshProperties();
  }

  /**
   * No public constructor.
   */
  private GoogleAnalyticsUtils() {}

  /**
   * Refresh the properties.
   * 
   * <p>This is package scope for testing and should not be called within an application. The
   * default Google Analytics URL should be effectively final and set by system properties at
   * start-up.
   */
  static void refreshProperties() {
    hostname =
        System.getProperty(PROPERTY_GOOGLE_ANALYTICS_HOSTNAME, DEFAULT_GOOGLE_ANALYTICS_HOSTNAME);
    file = System.getProperty(PROPERTY_GOOGLE_ANALYTICS_FILE, DEFAULT_GOOGLE_ANALYTICS_FILE);
    debugFile = System.getProperty(PROPERTY_GOOGLE_ANALYTICS_DEBUG_FILE,
        DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE);
  }


  /**
   * Gets the Google Analytics URL.
   * 
   * <p>This may throw a wrapped {@link MalformedURLException} if the hostname and file have been
   * changed from the defaults using System properties.
   *
   * @param secure Set to true to use HTTPS
   * @param debug Set to true to use the debug server URL
   * @return the url
   * @throws MalformedUrlRuntimeException If the URL was malformed
   * @see #PROPERTY_GOOGLE_ANALYTICS_HOSTNAME
   * @see #PROPERTY_GOOGLE_ANALYTICS_FILE
   */
  public static URL getGoogleAnalyticsUrl(boolean secure, boolean debug)
      throws MalformedUrlRuntimeException {
    try {
      String protocol;
      String urlFile;
      if (debug) {
        // Note: The debug server only responds to HTTPS
        protocol = HTTPS;
        urlFile = debugFile;
      } else {
        protocol = (secure) ? HTTPS : HTTP;
        urlFile = file;
      }
      return new URL(protocol, hostname, urlFile);
    } catch (final MalformedURLException ex) {
      logger.severe("Failed to create Google Analytics URL: " + ex.getMessage());
      throw new MalformedUrlRuntimeException(ex);
    }
  }

  /**
   * Gets the hostname for the Google Analytics URL.
   *
   * @return the hostname
   */
  public static String getHostname() {
    return hostname;
  }

  /**
   * Gets the file for the Google Analytics URL.
   *
   * @return the file
   */
  public static String getFile() {
    return file;
  }

  /**
   * Gets the debug file for the Google Analytics URL.
   *
   * @return the debug file
   */
  public static String getDebugFile() {
    return debugFile;
  }
}
