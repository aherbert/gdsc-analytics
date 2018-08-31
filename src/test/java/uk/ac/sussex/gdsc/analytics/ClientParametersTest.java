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

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ClientParametersTest {
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";

    @SuppressWarnings("unused")
    @Test
    public void testConstructor() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        Assertions.assertEquals(trackingId, cp.getTrackingId());
        Assertions.assertEquals(clientId, cp.getClientId());
        Assertions.assertEquals(applicationName, cp.getApplicationName());
        Assertions.assertTrue(cp.isNewSession());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ClientParameters(null, clientId, applicationName);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ClientParameters("", clientId, applicationName);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ClientParameters(trackingId, clientId, null);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ClientParameters(trackingId, clientId, "");
        });

        // Client ID is random UUID if not provided
        checkClientId(new ClientParameters(trackingId, null, applicationName));
        checkClientId(new ClientParameters(trackingId, "", applicationName));

        // Tracking Id should match [A-Z]+-[0-9]+-[0-9]+
        new ClientParameters("test bad tracking Id", clientId, applicationName);
    }

    private static void checkClientId(ClientParameters clientParameters) {
        final String clientId = clientParameters.getClientId();
        Assertions.assertNotNull(clientId);
        Assertions.assertNotNull(UUID.fromString(clientId));
    }

    /**
     * Test all properties invalidate the URL
     */
    @Test
    public void testProperties() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        final String url = "http://www.test.com?hello&world";
        Assertions.assertNull(cp.getUrl());
        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());

        final String screenResolution = "1";
        final String userLanguage = "2";
        final String hostName = "3";
        final String userAgent = "4";
        final String applicationId = "5";
        final String applicationVersion = "6";
        final boolean anonymised = true;

        Assertions.assertNull(cp.getScreenResolution());
        cp.setScreenResolution(screenResolution);
        Assertions.assertEquals(screenResolution, cp.getScreenResolution());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertNull(cp.getUserLanguage());
        cp.setUserLanguage(userLanguage);
        Assertions.assertEquals(userLanguage, cp.getUserLanguage());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertNull(cp.getHostName());
        cp.setHostName(hostName);
        Assertions.assertEquals(hostName, cp.getHostName());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertNull(cp.getUserAgent());
        cp.setUserAgent(userAgent);
        Assertions.assertEquals(userAgent, cp.getUserAgent());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertNull(cp.getApplicationId());
        cp.setApplicationId(applicationId);
        Assertions.assertEquals(applicationId, cp.getApplicationId());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertNull(cp.getApplicationVersion());
        cp.setApplicationVersion(applicationVersion);
        Assertions.assertEquals(applicationVersion, cp.getApplicationVersion());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertFalse(cp.isAnonymised());
        cp.setAnonymised(anonymised);
        Assertions.assertEquals(anonymised, cp.isAnonymised());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertFalse(cp.hasCustomDimensions());
        cp.addCustomDimension(1, null); // Ignored
        Assertions.assertFalse(cp.hasCustomDimensions());
        cp.addCustomDimension(3, "33");
        Assertions.assertTrue(cp.hasCustomDimensions());
        Assertions.assertNull(cp.getUrl());

        cp.setUrl(url);
        Assertions.assertEquals(url, cp.getUrl());
        Assertions.assertFalse(cp.hasCustomMetrics());
        cp.addCustomMetric(4, 44);
        Assertions.assertTrue(cp.hasCustomMetrics());
        Assertions.assertNull(cp.getUrl());
    }

    @Test
    public void testSession() {
        // This cannot really test the timeout but does test the
        // method to reset the session
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        Assertions.assertTrue(cp.isNewSession());
        Assertions.assertFalse(cp.isNewSession());
        cp.resetSession();
        Assertions.assertTrue(cp.isNewSession());
    }
}
