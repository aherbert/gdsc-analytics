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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class UrlUtilsTest {
  @Test
  public void testProxy() {
    // Test various proxy addresses
    // Valid
    Assertions.assertNotNull(UrlUtils.getProxy("http://localhost:80"));
    Assertions.assertNotNull(UrlUtils.getProxy("https://localhost:80"));
    Assertions.assertNotNull(UrlUtils.getProxy("localhost:80"));
    Assertions.assertNotNull(UrlUtils.getProxy("https://localhost:80/more/stuff"));

    // Invalid
    Assertions.assertNull(UrlUtils.getProxy(null));
    Assertions.assertNull(UrlUtils.getProxy(""));
    Assertions.assertNull(UrlUtils.getProxy("localhost"));
    Assertions.assertNull(UrlUtils.getProxy("http://localhost"));
    Assertions.assertNull(UrlUtils.getProxy("http://localhost :80"));
  }

  @Test
  public void testGetGoogleAnalyticsUrl() {
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_PROTOCOL, UrlUtils.getProtocol());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL,
        UrlUtils.getSecureProtocol());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_HOSTNAME, UrlUtils.getHostname());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_FILE, UrlUtils.getFile());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE, UrlUtils.getDebugFile());

    final String hostname = "hostname";
    final String file = "/file";
    final String debugFile = "/debugFile";
    System.setProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_HOSTNAME, hostname);
    System.setProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_FILE, file);
    System.setProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_DEBUG_FILE, debugFile);

    UrlUtils.refreshSystemProperties();

    // non-secure, non-debug
    Assertions.assertEquals("http://" + hostname + file,
        UrlUtils.getGoogleAnalyticsUrl(false, false).toString());

    // secure, non-debug
    Assertions.assertEquals("https://" + hostname + file,
        UrlUtils.getGoogleAnalyticsUrl(true, false).toString());

    // non-secure, debug - Still uses https
    Assertions.assertEquals("https://" + hostname + debugFile,
        UrlUtils.getGoogleAnalyticsUrl(false, true).toString());

    // secure, debug
    Assertions.assertEquals("https://" + hostname + debugFile,
        UrlUtils.getGoogleAnalyticsUrl(true, true).toString());

    // Test throws an exception with bad format
    System.setProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_PROTOCOL, "foo");
    System.setProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL, "bar");

    UrlUtils.refreshSystemProperties();

    // Non-secure
    MalformedUrlRuntimeException ex =
        Assertions.assertThrows(MalformedUrlRuntimeException.class, () -> {
          UrlUtils.getGoogleAnalyticsUrl(false, false);
        });
    Assertions.assertTrue(ex.getMessage().contains("unknown protocol: foo"));
    // Secure
    ex = Assertions.assertThrows(MalformedUrlRuntimeException.class, () -> {
      UrlUtils.getGoogleAnalyticsUrl(true, false);
    });
    Assertions.assertTrue(ex.getMessage().contains("unknown protocol: bar"));

    // Reset
    System.clearProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_PROTOCOL);
    System.clearProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_SECURE_PROTOCOL);
    System.clearProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_HOSTNAME);
    System.clearProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_FILE);
    System.clearProperty(UrlUtils.PROPERTY_GOOGLE_ANALYTICS_DEBUG_FILE);

    UrlUtils.refreshSystemProperties();

    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_PROTOCOL, UrlUtils.getProtocol());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_SECURE_PROTOCOL,
        UrlUtils.getSecureProtocol());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_HOSTNAME, UrlUtils.getHostname());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_FILE, UrlUtils.getFile());
    Assertions.assertEquals(UrlUtils.DEFAULT_GOOGLE_ANALYTICS_DEBUG_FILE, UrlUtils.getDebugFile());
  }
}
