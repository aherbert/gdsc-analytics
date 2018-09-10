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

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility functions for URLs.
 */
public final class UrlUtils {

  /** The logger. */
  private static final Logger logger = Logger.getLogger(UrlUtils.class.getName());

  /**
   * The system property constant for the Google Analytics protocol component of the URL.
   * 
   * <p>If this is not set then the value defaults to {@link #DEFAULT_GOOGLE_ANALYTICS_PROTOCOL}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_PROTOCOL = "gdsc.analytics.protocol";
  /**
   * The system property constant for the Google Analytics secure protocol component of the URL.
   * 
   * <p>If this is not set then the value defaults to {@link #DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL = "gdsc.analytics.secure.protocol";
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

  /** The protocol for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_PROTOCOL = "http";
  /** The protocol for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL = "https";
  /** The hostname for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_HOSTNAME = "www.google-analytics.com";
  /** The default file for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_FILE = "/collect";
  /** The default debug file for the Google Analytics URL. */
  public static final String DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE = "/debug/collect";

  /** The protocol for the Google Analytics URL. */
  private static String protocol;
  /** The secure protocol for the Google Analytics URL. */
  private static String secureProtocol;
  /** The hostname for the Google Analytics URL. */
  private static String hostname;
  /** The file for the Google Analytics URL. */
  private static String file;
  /** The debug file for the Google Analytics URL. */
  private static String debugFile;

  static {
    refreshSystemProperties();
  }

  /**
   * No public construction.
   */
  private UrlUtils() {
    // Do nothing
  }

  /**
   * Refresh the properties.
   * 
   * <p>This is package scope for testing and should not be called within an application. The
   * default Google Analytics URL should be effectively final and set by system properties at
   * start-up.
   */
  static void refreshSystemProperties() {
    protocol =
        System.getProperty(PROPERTY_GOOGLE_ANALYTICS_PROTOCOL, DEFAULT_GOOGLE_ANALYTICS_PROTOCOL);
    secureProtocol =
        System.getProperty(PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL, DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL);
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
      String urlProtocol;
      String urlFile;
      if (debug) {
        // Note: The debug server only responds to HTTPS
        urlProtocol = secureProtocol;
        urlFile = debugFile;
      } else {
        urlProtocol = (secure) ? secureProtocol : protocol;
        urlFile = file;
      }
      return new URL(urlProtocol, hostname, urlFile);
    } catch (final MalformedURLException ex) {
      logger.severe("Failed to create Google Analytics URL: " + ex.getMessage());
      throw new MalformedUrlRuntimeException(ex);
    }
  }

  /**
   * Gets the protocol for the Google Analytics URL.
   *
   * @return the protocol
   */
  public static String getProtocol() {
    return protocol;
  }

  /**
   * Gets the secure protocol for the Google Analytics URL.
   *
   * @return the secure protocol
   */
  public static String getSecureProtocol() {
    return secureProtocol;
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

  /**
   * Construct a proxy from an address.
   * 
   * <p>If no proxy can be set then this will return null.
   *
   * @param proxyAddress "hostname:port" of the proxy to use; may also be given as URL
   *        ("http://hostname:port/").
   * @return the proxy (or null)
   */
  public static Proxy getProxy(String proxyAddress) {
    if (proxyAddress != null) {
      // Split into "hostname:port"
      final Matcher m = Pattern.compile("^(https?://|)([^ :]+):([0-9]+)").matcher(proxyAddress);
      if (m.find()) {
        final String hostname = m.group(2);
        final int port = Integer.parseInt(m.group(3));

        final SocketAddress sa = new InetSocketAddress(hostname, port);
        return new Proxy(Type.HTTP, sa);
      }
    }
    return null;
  }
}
