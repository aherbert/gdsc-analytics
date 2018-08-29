/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics Measurement
 * protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert, Daniel Murphy
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
public class ClientParametersTest
{
	private String trackingId = "AAA-123-456";
	private String clientId = "Anything";
	private String applicationName = "Test";

	@Test
	public void testConstructor()
	{
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
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

		Assertions.assertNotNull(new ClientParameters(trackingId, null, applicationName).getClientId());
		Assertions.assertNotNull(new ClientParameters(trackingId, "", applicationName).getClientId());
	}

	/**
	 * Test all properties invalidate the URL
	 */
	@Test
	public void testProperties()
	{
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

		String url = "http://www.test.com?hello&world";
		Assertions.assertNull(cp.getUrl());
		cp.setUrl(url);
		Assertions.assertEquals(url, cp.getUrl());

		String screenResolution = "1";
		String userLanguage = "2";
		String hostName = "3";
		String userAgent = "4";
		String applicationId = "5";
		String applicationVersion = "6";
		boolean anonymized = true;
		
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
		Assertions.assertFalse(cp.isAnonymized());
		cp.setAnonymized(anonymized);
		Assertions.assertEquals(anonymized, cp.isAnonymized());
		Assertions.assertNull(cp.getUrl());
		
		cp.setUrl(url);
		Assertions.assertEquals(url, cp.getUrl());
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
	public void testSession()
	{
		// This cannot really test the timeout but does test the 
		// method to reset the session
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
		Assertions.assertTrue(cp.isNewSession());
		Assertions.assertFalse(cp.isNewSession());
		cp.resetSession();
		Assertions.assertTrue(cp.isNewSession());
	}
}