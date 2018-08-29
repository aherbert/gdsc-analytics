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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.UrlAssert;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class AnalyticsMeasurementProtocolURLBuilderTest
{
	private String host = "http://www.google-analytics.com?";
	private String version = "1";
	private String trackingId = "AAA-123-456";
	private String clientId = "Anything";
	private String applicationName = "Test";
	private String screenResolution = "c1";
	private String userLanguage = "c2";
	private String hostName = "c3";
	private String userAgent = "c4";
	private String applicationId = "c5";
	private String applicationVersion = "c6";
	private int clientCDi1 = 3;
	private String clientCDv1 = "test42";
	private int clientCDi2 = 6;
	private String clientCDv2 = "more17";
	private int clientCMi1 = 77;
	private int clientCMv1 = 156;
	private int clientCMi2 = 99;
	private int clientCMv2 = 197;
	private String documentPath = "r1";
	private String documentTitle = "r2";
	private String category = "r3";
	private String action = "r4";
	private String label = "r5";
	private Integer value = 99;
	private int paramCDi1 = 77;
	private String paramCDv1 = "again78";
	private int paramCDi2 = 103;
	private String paramCDv2 = "extra99";
	private int paramCMi1 = 14;
	private int paramCMv1 = 19090;
	private int paramCMi2 = 71;
	private int paramCMv2 = 19567;

	@Test
	public void testConstructor()
	{
		AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();
		Assertions.assertThat(b.getVersion()).isEqualTo(version);
	}

	@Test
	public void testBuildURLPageView() throws MalformedURLException
	{
		AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

		// Create the client
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

		boolean anonymized = true;

		cp.setScreenResolution(screenResolution);
		cp.setUserLanguage(userLanguage);
		cp.setHostName(hostName);
		cp.setUserAgent(userAgent);
		cp.setApplicationId(applicationId);
		cp.setApplicationVersion(applicationVersion);
		cp.setAnonymized(anonymized);

		cp.addCustomDimension(clientCDi1, clientCDv1);
		cp.addCustomDimension(clientCDi2, clientCDv2);
		cp.addCustomMetric(clientCMi1, clientCMv1);
		cp.addCustomMetric(clientCMi2, clientCMv2);

		// Simple start-of-session pageview
		RequestParameters rp = new RequestParameters(HitType.PAGEVIEW);

		rp.setDocumentPath(documentPath);
		rp.setDocumentTitle(documentTitle);

		rp.addCustomDimension(paramCDi1, paramCDv1);
		rp.addCustomDimension(paramCDi2, paramCDv2);
		rp.addCustomMetric(paramCMi1, paramCMv1);
		rp.addCustomMetric(paramCMi2, paramCMv2);

		String url = b.buildURL(cp, rp, System.currentTimeMillis());

		//@formatter:off
		UrlAssert a = (UrlAssert) Assertions.assertThat(new URL(host + url));
		a
		.hasParameter("v", version)
		.hasParameter("sc", "start")
		.hasParameter("tid", trackingId)
		.hasParameter("cid", clientId)
		.hasParameter("an", applicationName)
		.hasParameter("aid", applicationId)
		.hasParameter("av", applicationVersion)
		.hasParameter("sr", screenResolution)
		.hasParameter("ul", userLanguage)
		.hasParameter("ua", userAgent)
		.hasParameter("je", "1")
		.hasParameter("cd"+clientCDi1, clientCDv1)
		.hasParameter("cd"+clientCDi2, clientCDv2)
		.hasParameter("cm"+clientCMi1, Integer.toString(clientCMv1))
		.hasParameter("cm"+clientCMi2, Integer.toString(clientCMv2))
		.hasParameter("cd"+paramCDi1, paramCDv1)
		.hasParameter("cd"+paramCDi2, paramCDv2)
		.hasParameter("cm"+paramCMi1, Integer.toString(paramCMv1))
		.hasParameter("cm"+paramCMi2, Integer.toString(paramCMv2))
		.hasParameter("t", rp.getHitType())
		.hasParameter("dp", documentPath)
		.hasParameter("dt", documentTitle)
		.hasParameter("qt")
		;
		if (anonymized) {
			a.hasParameter("aip", "1");
			a.hasNoParameter("dh");
		} else {
			if (cp.getHostName() != null)
				a.hasParameter("dh", cp.getHostName());
			a.hasNoParameter("aip");
		}
					
		//@formatter:on

		// Second pageview
		rp = new RequestParameters(HitType.PAGEVIEW);

		rp.setDocumentPath(documentPath + "2");
		rp.setDocumentTitle(documentTitle + "2");

		url = b.buildURL(cp, rp, System.currentTimeMillis());

		//@formatter:off
		a = (UrlAssert) Assertions.assertThat(new URL(host + url));
		a
		.hasParameter("v", version)
		// No a new session
		.hasNoParameter("sc")
		// Same client
		.hasParameter("tid", trackingId)
		.hasParameter("cid", clientId)
		.hasParameter("an", applicationName)
		.hasParameter("aid", applicationId)
		.hasParameter("av", applicationVersion)
		.hasParameter("sr", screenResolution)
		.hasParameter("ul", userLanguage)
		.hasParameter("ua", userAgent)
		.hasParameter("je", "1")
		// No session level parameters
		.hasNoParameter("cd"+clientCDi1)
		.hasNoParameter("cd"+clientCDi2)
		.hasNoParameter("cm"+clientCMi1)
		.hasNoParameter("cm"+clientCMi2)
		// This hit has no custom parameters
		.hasNoParameter("cd"+paramCDi1)
		.hasNoParameter("cd"+paramCDi2)
		.hasNoParameter("cm"+paramCMi1)
		.hasNoParameter("cm"+paramCMi2)
		.hasParameter("t", rp.getHitType())
		.hasParameter("dp", documentPath + "2")
		.hasParameter("dt", documentTitle + "2")
		.hasParameter("qt")
		;
		if (anonymized) {
			a.hasParameter("aip", "1");
			a.hasNoParameter("dh");
		} else {
			if (cp.getHostName() != null)
				a.hasParameter("dh", cp.getHostName());
			a.hasNoParameter("aip");
		}
	}


	@Test
	public void testBuildURLEvent() throws MalformedURLException
	{
		AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

		// Create the client
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

		boolean anonymized = false;
		
		cp.setHostName(hostName);
		cp.setAnonymized(anonymized);

		// Simple start-of-session pageview
		RequestParameters rp = new RequestParameters(HitType.EVENT);

		rp.setCategory(category);
		rp.setAction(action);
		rp.setLabel(label);
		rp.setValue(value);

		String url = b.buildGetURL(cp, rp, System.currentTimeMillis());

		//@formatter:off
		UrlAssert a = (UrlAssert) Assertions.assertThat(new URL(host + url));
		a
		.hasParameter("v", version)
		.hasParameter("sc", "start")
		.hasParameter("tid", trackingId)
		.hasParameter("cid", clientId)
		.hasParameter("an", applicationName)
		.hasNoParameter("aid")
		.hasNoParameter("av")
		.hasNoParameter("sr")
		.hasNoParameter("ul")
		.hasNoParameter("ua")
		.hasParameter("je", "1")
		.hasParameter("t", rp.getHitType())
		.hasParameter("ec", category)
		.hasParameter("ea", action)
		.hasParameter("el", label)
		.hasParameter("ev", value.toString())
		.hasParameter("qt")
		.hasParameter("z")
		;
		if (anonymized) {
			a.hasParameter("aip", "1");
			a.hasNoParameter("dh");
		} else {
			if (cp.getHostName() != null)
				a.hasParameter("dh", cp.getHostName());
			a.hasNoParameter("aip");
		}
		//@formatter:on

		String url2 = b.buildGetURL(cp, rp, System.currentTimeMillis());
		String z1 = getZ(url);
		String z2 = getZ(url2);
		Assertions.assertThat(z1).isNotEqualTo(z2);
	}

	private String getZ(String url)
	{
		int i = url.indexOf("z=");
		Assertions.assertThat(i).isNotEqualTo(-1);
		// Assume that the z parameter is on the end
		return url.substring(i + 1, url.length());
	}

	@Test
	public void testBuildURLThrowsForUnsupportedHitType()
	{
		AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

		// Create the client
		ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

		// Throw exception for unsupported hit type
		EnumSet<HitType> set = EnumSet.allOf(HitType.class);
		set.remove(HitType.PAGEVIEW);
		set.remove(HitType.EVENT);

		for (HitType ht : set)
		{
			RequestParameters rp = new RequestParameters(ht);
			Assertions.assertThatThrownBy(() -> {
				b.buildURL(cp, rp, System.currentTimeMillis());
			}).isInstanceOf(IllegalArgumentException.class);
		}
	}
}