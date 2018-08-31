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
import java.util.EnumSet;

import org.assertj.core.api.AbstractUrlAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class AnalyticsMeasurementProtocolURLBuilderTest {
    private final String host = "http://www.google-analytics.com?";
    private final String version = "1";
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";
    private final String screenResolution = "c1";
    private final String userLanguage = "c2";
    private final String hostName = "c3";
    private final String userAgent = "c4";
    private final String applicationId = "c5";
    private final String applicationVersion = "c6";
    private final int clientCDi1 = 3;
    private final String clientCDv1 = "test42";
    private final int clientCDi2 = 6;
    private final String clientCDv2 = "more17";
    private final int clientCMi1 = 77;
    private final int clientCMv1 = 156;
    private final int clientCMi2 = 99;
    private final int clientCMv2 = 197;
    private final String documentPath = "r1";
    private final String documentTitle = "r2";
    private final String category = "r3";
    private final String action = "r4";
    private final String label = "r5";
    private final Integer value = 99;
    private final int paramCDi1 = 77;
    private final String paramCDv1 = "again78";
    private final int paramCDi2 = 103;
    private final String paramCDv2 = "extra99";
    private final int paramCMi1 = 14;
    private final int paramCMv1 = 19090;
    private final int paramCMi2 = 71;
    private final int paramCMv2 = 19567;

    @Test
    public void testConstructor() {
        final AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();
        Assertions.assertThat(b.getVersion()).isEqualTo(version);
    }

    @Test
    public void testBuildURLPageView() throws MalformedURLException {
        final AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

        // Create the client
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        final boolean anonymised = true;

        cp.setScreenResolution(screenResolution);
        cp.setUserLanguage(userLanguage);
        cp.setHostName(hostName);
        cp.setUserAgent(userAgent);
        cp.setApplicationId(applicationId);
        cp.setApplicationVersion(applicationVersion);
        cp.setAnonymised(anonymised);

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
        AbstractUrlAssert<?> a = Assertions.assertThat(new URL(host + url));
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
        if (anonymised) {
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
        a = Assertions.assertThat(new URL(host + url));
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
        if (anonymised) {
            a.hasParameter("aip", "1");
            a.hasNoParameter("dh");
        } else {
            if (cp.getHostName() != null)
                a.hasParameter("dh", cp.getHostName());
            a.hasNoParameter("aip");
            //@formatter:on
        }
    }

    @Test
    public void testBuildURLEvent() throws MalformedURLException {
        final AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

        // Create the client
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        final boolean anonymised = false;

        cp.setHostName(hostName);
        cp.setAnonymised(anonymised);

        // Simple start-of-session pageview
        final RequestParameters rp = new RequestParameters(HitType.EVENT);

        rp.setCategory(category);
        rp.setAction(action);
        rp.setLabel(label);
        rp.setValue(value);

        final String url = b.buildGetURL(cp, rp, System.currentTimeMillis());

        //@formatter:off
        AbstractUrlAssert<?> a = Assertions.assertThat(new URL(host + url));
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
        if (anonymised) {
            a.hasParameter("aip", "1");
            a.hasNoParameter("dh");
        } else {
            if (cp.getHostName() != null)
                a.hasParameter("dh", cp.getHostName());
            a.hasNoParameter("aip");
        }

        // Test the value will be ignored if null
        rp.setValue(null);

        final String url2 = b.buildGetURL(cp, rp, System.currentTimeMillis());
        final String z1 = getZ(url);
        final String z2 = getZ(url2);
        Assertions.assertThat(z1).isNotEqualTo(z2);

        a = Assertions.assertThat(new URL(host + url2));
        a
        .hasParameter("v", version)
        // No a new session
        .hasNoParameter("sc")
        // Same client
        .hasParameter("tid", trackingId)
        .hasParameter("cid", clientId)
        .hasParameter("an", applicationName)
        .hasNoParameter("aid")
        .hasNoParameter("av")
        .hasNoParameter("sr")
        .hasNoParameter("ul")
        .hasNoParameter("ua")
        .hasParameter("je", "1")
        // Event hit with no value
        .hasParameter("t", rp.getHitType())
        .hasParameter("ec", category)
        .hasParameter("ea", action)
        .hasParameter("el", label)
        // No event value
        .hasNoParameter("ev")
        .hasParameter("qt")
        .hasParameter("z")
        ;
        //@formatter:on
    }

    private static String getZ(String url) {
        final int i = url.indexOf("z=");
        Assertions.assertThat(i).isNotEqualTo(-1);
        // Assume that the z parameter is on the end
        return url.substring(i + 1, url.length());
    }

    @Test
    public void testBuildURLThrowsForUnsupportedHitType() {
        final AnalyticsMeasurementProtocolURLBuilder b = new AnalyticsMeasurementProtocolURLBuilder();

        // Create the client
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        // Throw exception for unsupported hit type
        final EnumSet<HitType> set = EnumSet.allOf(HitType.class);
        set.remove(HitType.PAGEVIEW);
        set.remove(HitType.EVENT);

        for (final HitType ht : set) {
            final RequestParameters rp = new RequestParameters(ht);
            Assertions.assertThatThrownBy(() -> {
                b.buildURL(cp, rp, System.currentTimeMillis());
            }).isInstanceOf(IllegalArgumentException.class);
        }

        // Also test null hit type in the switch statement for coverage
        final RequestParameters rp = new RequestParameters(HitType.PAGEVIEW) {
            @Override
            public HitType getHitTypeEnum() {
                return null;
            }
        };
        Assertions.assertThatThrownBy(() -> {
            b.buildURL(cp, rp, System.currentTimeMillis());
        }).isInstanceOf(NullPointerException.class);
    }
}
