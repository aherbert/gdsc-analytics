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

import java.net.Proxy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.ac.sussex.gdsc.analytics.JGoogleAnalyticsTracker.DispatchMode;
import uk.ac.sussex.gdsc.analytics.JGoogleAnalyticsTracker.MeasurementProtocolVersion;

@SuppressWarnings("javadoc")
public class JGoogleAnalyticsTrackerTest {
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";

    @SuppressWarnings("unused")
    @Test
    public void testConstructor() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final MeasurementProtocolVersion version = MeasurementProtocolVersion.V_1;
        JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(cp, version);
        Assertions.assertNotNull(tracker);
        Assertions.assertTrue(tracker.isEnabled());
        Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());

        // Require arguments
        Assertions.assertThrows(NullPointerException.class, () -> {
            new JGoogleAnalyticsTracker(null, version);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new JGoogleAnalyticsTracker(cp, null);
        });

        // Overloaded constructor
        tracker = new JGoogleAnalyticsTracker(cp, version, DispatchMode.MULTI_THREAD);
        Assertions.assertEquals(DispatchMode.MULTI_THREAD, tracker.getDispatchMode());
    }

    @Test
    public void testProperties() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);
        final MeasurementProtocolVersion version = MeasurementProtocolVersion.V_1;

        final JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(cp, version);

        tracker.setDispatchMode(DispatchMode.SYNCHRONOUS);
        Assertions.assertEquals(DispatchMode.SYNCHRONOUS, tracker.getDispatchMode());
        Assertions.assertTrue(tracker.isSynchronous());
        Assertions.assertFalse(tracker.isSingleThreaded());
        Assertions.assertFalse(tracker.isMultiThreaded());

        tracker.setDispatchMode(DispatchMode.SINGLE_THREAD);
        Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
        Assertions.assertFalse(tracker.isSynchronous());
        Assertions.assertTrue(tracker.isSingleThreaded());
        Assertions.assertFalse(tracker.isMultiThreaded());

        tracker.setDispatchMode(DispatchMode.MULTI_THREAD);
        Assertions.assertEquals(DispatchMode.MULTI_THREAD, tracker.getDispatchMode());
        Assertions.assertFalse(tracker.isSynchronous());
        Assertions.assertFalse(tracker.isSingleThreaded());
        Assertions.assertTrue(tracker.isMultiThreaded());

        // On by default
        Assertions.assertTrue(tracker.isEnabled());
        tracker.setEnabled(false);
        Assertions.assertFalse(tracker.isEnabled());

        // Off by default
        Assertions.assertFalse(tracker.isSecure());
        tracker.setSecure(true);
        Assertions.assertTrue(tracker.isSecure());
    }

    @Test
    public void testProxy() {
        JGoogleAnalyticsTracker.setProxy((Proxy) null);
        JGoogleAnalyticsTracker.setProxy(Proxy.NO_PROXY);

        // Test various proxy addresses
        // Valid
        Assertions.assertTrue(JGoogleAnalyticsTracker.setProxy("http://localhost:80"));
        Assertions.assertTrue(JGoogleAnalyticsTracker.setProxy("https://localhost:80"));
        Assertions.assertTrue(JGoogleAnalyticsTracker.setProxy("localhost:80"));
        Assertions.assertTrue(JGoogleAnalyticsTracker.setProxy("https://localhost:80/more/stuff"));

        // Invalid
        Assertions.assertFalse(JGoogleAnalyticsTracker.setProxy(""));
        Assertions.assertFalse(JGoogleAnalyticsTracker.setProxy("localhost"));
        Assertions.assertFalse(JGoogleAnalyticsTracker.setProxy("http://localhost"));
        Assertions.assertFalse(JGoogleAnalyticsTracker.setProxy("http://localhost :80"));

        // Reset
        JGoogleAnalyticsTracker.setProxy(Proxy.NO_PROXY);
    }

    // Q. How to test the dispatch?
    // Can this use the proxy with a mock proxy?
    // A. Create a mock dispatch receiver
}