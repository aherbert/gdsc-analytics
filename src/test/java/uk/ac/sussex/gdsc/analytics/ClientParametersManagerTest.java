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
public class ClientParametersManagerTest {
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";

    @Test
    public void testPopulate() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        Assertions.assertNull(cp.getUserLanguage());
        Assertions.assertNull(cp.getScreenResolution());
        Assertions.assertNull(cp.getUserAgent());

        ClientParametersManager.populate(cp);

        Assertions.assertNotNull(cp.getUserLanguage());
        Assertions.assertNotNull(cp.getScreenResolution());
        Assertions.assertNotNull(cp.getUserAgent());
    }

    @Test
    public void testPopulateHostName() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        Assertions.assertNull(cp.getHostName());

        ClientParametersManager.populateHostname(cp);

        Assertions.assertNotNull(cp.getHostName());
    }

    @Test
    public void testIsWindows() {
        Assertions.assertTrue(ClientParametersManager.isWindows("Windows tjlatl"));
        Assertions.assertFalse(ClientParametersManager.isWindows("  Windows tjlatl"));
    }

    @Test
    public void testIsLinux() {
        Assertions.assertTrue(ClientParametersManager.isLinux("Linux tjlatl"));
        Assertions.assertFalse(ClientParametersManager.isLinux("  Linux tjlatl"));
    }

    @Test
    public void testGetPlatform() {
        // Just hit the different tests for windows
        Assertions.assertEquals("Windows NT 10.0", ClientParametersManager.getPlatform("WINDOWS 10"));
        Assertions.assertEquals("Windows NT 10.0", ClientParametersManager.getPlatform("WINDOWS server 2016"));
        Assertions.assertEquals("Windows NT 6.3", ClientParametersManager.getPlatform("WINDOWS 8.1"));
        Assertions.assertEquals("Windows NT 6.3", ClientParametersManager.getPlatform("WINDOWS server 2012 r2"));
        Assertions.assertEquals("Windows NT 6.2", ClientParametersManager.getPlatform("WINDOWS 8"));
        Assertions.assertEquals("Windows NT 6.2", ClientParametersManager.getPlatform("WINDOWS server 2012"));
        Assertions.assertEquals("Windows NT 6.1", ClientParametersManager.getPlatform("WINDOWS 7"));
        Assertions.assertEquals("Windows NT 6.1", ClientParametersManager.getPlatform("WINDOWS server 2011"));
        Assertions.assertEquals("Windows NT 6.1", ClientParametersManager.getPlatform("WINDOWS server 2008 r2"));
        Assertions.assertEquals("Windows NT 6.0", ClientParametersManager.getPlatform("WINDOWS vista"));
        Assertions.assertEquals("Windows NT 6.0", ClientParametersManager.getPlatform("WINDOWS server 2008"));
        Assertions.assertEquals("Windows NT 5.2", ClientParametersManager.getPlatform("WINDOWS server 2003"));
        Assertions.assertEquals("Windows NT 5.2", ClientParametersManager.getPlatform("WINDOWS xp x64"));
        Assertions.assertEquals("Windows NT 5.1", ClientParametersManager.getPlatform("WINDOWS xp"));
        Assertions.assertEquals("Windows NT 5.01", ClientParametersManager.getPlatform("WINDOWS 2000, service"));
        Assertions.assertEquals("Windows NT 5.0", ClientParametersManager.getPlatform("WINDOWS 2000"));
        Assertions.assertEquals("Windows NT 4.0", ClientParametersManager.getPlatform("WINDOWS nt 4"));
        Assertions.assertEquals("Windows 98; Win 9x 4.90", ClientParametersManager.getPlatform("WINDOWS mw"));
        Assertions.assertEquals("Windows 98", ClientParametersManager.getPlatform("WINDOWS 98"));
        Assertions.assertEquals("Windows 95", ClientParametersManager.getPlatform("WINDOWS 95"));
        Assertions.assertEquals("Windows CE", ClientParametersManager.getPlatform("WINDOWS ce"));
        // This is the default
        Assertions.assertEquals("Windows NT 6.1", ClientParametersManager.getPlatform("WINDOWS"));

        Assertions.assertTrue(ClientParametersManager.getPlatform("MAC").startsWith("Macintosh"));
        Assertions.assertTrue(ClientParametersManager.getPlatform("mac").startsWith("Macintosh"));

        Assertions.assertTrue(ClientParametersManager.getPlatform("Linux and stuff").startsWith("Linux"));
        // This is untouched
        Assertions.assertTrue(ClientParametersManager.getPlatform("other platform").startsWith("other platform"));
    }

    @Test
    public void testGetScreenSize() {
        Assertions.assertNotNull(ClientParametersManager.getScreenSize("Windows"));
        Assertions.assertNotNull(ClientParametersManager.getScreenSize("Linux"));
    }
}
