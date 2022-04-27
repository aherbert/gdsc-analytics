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

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.util.logging.Level;
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
   * <p>If this is not set then the value defaults to
   * {@link #DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL}.
   */
  public static final String PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL =
      "gdsc.analytics.secure.protocol";
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
    secureProtocol = System.getProperty(PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL,
        DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL);
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
  public static URL getGoogleAnalyticsUrl(boolean secure, boolean debug) {
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
      logger.log(Level.SEVERE, () -> "Failed to create Google Analytics URL: " + ex.getMessage());
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
