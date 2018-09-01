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
public class ClientParametersHelperTest {
  private final String trackingId = "AAA-123-456";
  private final String clientId = "Anything";
  private final String applicationName = "Test";
  private final String USER_REGION = "user.region";
  private final String USER_COUNTRY = "user.country";

  @Test
  public void testPopulate() {
    final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

    Assertions.assertNull(cp.getUserLanguage());
    Assertions.assertNull(cp.getScreenResolution());
    Assertions.assertNull(cp.getUserAgent());

    ClientParametersHelper.populate(cp);

    Assertions.assertNotNull(cp.getUserLanguage());
    Assertions.assertNotNull(cp.getScreenResolution());
    Assertions.assertNotNull(cp.getUserAgent());

    // Test with "user.region" null and not null
    final String testValue = "xxxx"; // Must be lowercase
    String userRegion = System.getProperty(USER_REGION);
    String userCountry = System.getProperty(USER_COUNTRY);
    if (userRegion == null) {
      System.setProperty(USER_REGION, testValue);
    } else {
      System.clearProperty(USER_REGION);
      System.setProperty(USER_COUNTRY, testValue);
    }
    ClientParametersHelper.populate(cp);
    Assertions.assertTrue(cp.getUserLanguage().contains(testValue));
    // Reset
    if (userRegion != null) {
      System.setProperty(USER_REGION, userRegion);
    }
    if (userCountry != null) {
      System.setProperty(USER_COUNTRY, userCountry);
    }
  }

  @Test
  public void testPopulateHostName() {
    final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

    Assertions.assertNull(cp.getHostName());

    ClientParametersHelper.populateHostname(cp);

    Assertions.assertNotNull(cp.getHostName());
  }

  @Test
  public void testIsWindows() {
    Assertions.assertTrue(ClientParametersHelper.isWindows("Windows tjlatl"));
    Assertions.assertFalse(ClientParametersHelper.isWindows("  Windows tjlatl"));
  }

  @Test
  public void testIsLinux() {
    Assertions.assertTrue(ClientParametersHelper.isLinux("Linux tjlatl"));
    Assertions.assertFalse(ClientParametersHelper.isLinux("  Linux tjlatl"));
  }

  @Test
  public void testGetPlatform() {
    // Just hit the different tests for windows
    Assertions.assertEquals("Windows NT 10.0", ClientParametersHelper.getPlatform("WINDOWS 10"));
    Assertions.assertEquals("Windows NT 10.0",
        ClientParametersHelper.getPlatform("WINDOWS server 2016"));
    Assertions.assertEquals("Windows NT 6.3", ClientParametersHelper.getPlatform("WINDOWS 8.1"));
    Assertions.assertEquals("Windows NT 6.3",
        ClientParametersHelper.getPlatform("WINDOWS server 2012 r2"));
    Assertions.assertEquals("Windows NT 6.2", ClientParametersHelper.getPlatform("WINDOWS 8"));
    Assertions.assertEquals("Windows NT 6.2",
        ClientParametersHelper.getPlatform("WINDOWS server 2012"));
    Assertions.assertEquals("Windows NT 6.1", ClientParametersHelper.getPlatform("WINDOWS 7"));
    Assertions.assertEquals("Windows NT 6.1",
        ClientParametersHelper.getPlatform("WINDOWS server 2011"));
    Assertions.assertEquals("Windows NT 6.1",
        ClientParametersHelper.getPlatform("WINDOWS server 2008 r2"));
    Assertions.assertEquals("Windows NT 6.0", ClientParametersHelper.getPlatform("WINDOWS vista"));
    Assertions.assertEquals("Windows NT 6.0",
        ClientParametersHelper.getPlatform("WINDOWS server 2008"));
    Assertions.assertEquals("Windows NT 5.2",
        ClientParametersHelper.getPlatform("WINDOWS server 2003"));
    Assertions.assertEquals("Windows NT 5.2", ClientParametersHelper.getPlatform("WINDOWS xp x64"));
    Assertions.assertEquals("Windows NT 5.1", ClientParametersHelper.getPlatform("WINDOWS xp"));
    Assertions.assertEquals("Windows NT 5.01",
        ClientParametersHelper.getPlatform("WINDOWS 2000, service"));
    Assertions.assertEquals("Windows NT 5.0", ClientParametersHelper.getPlatform("WINDOWS 2000"));
    Assertions.assertEquals("Windows NT 4.0", ClientParametersHelper.getPlatform("WINDOWS nt 4"));
    Assertions.assertEquals("Windows 98; Win 9x 4.90",
        ClientParametersHelper.getPlatform("WINDOWS mw"));
    Assertions.assertEquals("Windows 98", ClientParametersHelper.getPlatform("WINDOWS 98"));
    Assertions.assertEquals("Windows 95", ClientParametersHelper.getPlatform("WINDOWS 95"));
    Assertions.assertEquals("Windows CE", ClientParametersHelper.getPlatform("WINDOWS ce"));
    // This is the default
    Assertions.assertEquals("Windows NT 6.1", ClientParametersHelper.getPlatform("WINDOWS"));

    Assertions.assertTrue(ClientParametersHelper.getPlatform("MAC").startsWith("Macintosh"));
    Assertions.assertTrue(ClientParametersHelper.getPlatform("mac").startsWith("Macintosh"));

    Assertions
        .assertTrue(ClientParametersHelper.getPlatform("Linux and stuff").startsWith("Linux"));
    // This is untouched
    Assertions.assertTrue(
        ClientParametersHelper.getPlatform("other platform").startsWith("other platform"));
  }

  @Test
  public void testGetScreenSize() {
    Assertions.assertNotNull(ClientParametersHelper.getScreenSize("Windows"));
    Assertions.assertNotNull(ClientParametersHelper.getScreenSize("Linux"));
  }
}
