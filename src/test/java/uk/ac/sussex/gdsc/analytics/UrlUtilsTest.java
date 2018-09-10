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

    String hostname = "hostname";
    String file = "/file";
    String debugFile = "/debugFile";
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
