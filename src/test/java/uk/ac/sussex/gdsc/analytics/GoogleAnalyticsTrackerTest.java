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

import java.net.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.ac.sussex.gdsc.analytics.GoogleAnalyticsTracker.DispatchMode;
import uk.ac.sussex.gdsc.analytics.GoogleAnalyticsTracker.MeasurementProtocolVersion;

@SuppressWarnings("javadoc")
public class GoogleAnalyticsTrackerTest {
    private final String trackingId = "AAA-123-456";
    private final String clientId = "Anything";
    private final String applicationName = "Test";

    @SuppressWarnings("unused")
    @Test
    public void testConstructor() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp);
        Assertions.assertNotNull(tracker);
        Assertions.assertTrue(tracker.isEnabled());
        Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());

        // Overloaded constructor
        final DispatchMode mode = DispatchMode.MULTI_THREAD;
        tracker = new GoogleAnalyticsTracker(cp, mode);
        Assertions.assertEquals(mode, tracker.getDispatchMode());

        final DispatchMode mode2 = DispatchMode.SINGLE_THREAD;
        MeasurementProtocolVersion version = MeasurementProtocolVersion.V_1;
        tracker = new GoogleAnalyticsTracker(cp, mode2, version);
        Assertions.assertEquals(mode2, tracker.getDispatchMode());

        // Require some arguments (not dispatch mode)
        Assertions.assertThrows(NullPointerException.class, () -> {
            new GoogleAnalyticsTracker(null, mode, version);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new GoogleAnalyticsTracker(cp, mode, null);
        });
    }

    @Test
    public void testProperties() {
        final ClientParameters cp = new ClientParameters(trackingId, clientId, applicationName);

        final GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(cp);

        tracker.setDispatchMode(DispatchMode.SYNCHRONOUS);
        Assertions.assertEquals(DispatchMode.SYNCHRONOUS, tracker.getDispatchMode());
        Assertions.assertTrue(tracker.isSynchronous());
        Assertions.assertFalse(tracker.isAsynchronous());
        Assertions.assertFalse(tracker.isSingleThreaded());
        Assertions.assertFalse(tracker.isMultiThreaded());

        tracker.setDispatchMode(DispatchMode.SINGLE_THREAD);
        Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
        Assertions.assertFalse(tracker.isSynchronous());
        Assertions.assertTrue(tracker.isAsynchronous());
        Assertions.assertTrue(tracker.isSingleThreaded());
        Assertions.assertFalse(tracker.isMultiThreaded());

        tracker.setDispatchMode(DispatchMode.MULTI_THREAD);
        Assertions.assertEquals(DispatchMode.MULTI_THREAD, tracker.getDispatchMode());
        Assertions.assertFalse(tracker.isSynchronous());
        Assertions.assertTrue(tracker.isAsynchronous());
        Assertions.assertFalse(tracker.isSingleThreaded());
        Assertions.assertTrue(tracker.isMultiThreaded());

        // Test default to single thread.
        tracker.setDispatchMode(null);
        Assertions.assertEquals(DispatchMode.SINGLE_THREAD, tracker.getDispatchMode());
        Assertions.assertFalse(tracker.isSynchronous());
        Assertions.assertTrue(tracker.isAsynchronous());
        Assertions.assertTrue(tracker.isSingleThreaded());
        Assertions.assertFalse(tracker.isMultiThreaded());

        // On by default
        Assertions.assertTrue(tracker.isEnabled());
        tracker.setEnabled(false);
        Assertions.assertFalse(tracker.isEnabled());

        // Off by default
        Assertions.assertFalse(tracker.isSecure());
        tracker.setSecure(true);
        Assertions.assertTrue(tracker.isSecure());

        final Logger logger = Logger.getLogger(GoogleAnalyticsTrackerTest.class.getName());
        tracker.setLogger(logger);
        Assertions.assertEquals(logger, tracker.getLogger());
        tracker.setLogger(null);
        Assertions.assertNotNull(tracker.getLogger());
        Assertions.assertFalse(tracker.getLogger().isLoggable(Level.SEVERE));

        // Test the properties of the client get invoked
        final boolean[] resetSession = new boolean[1];
        final boolean[] setAnonymised = new boolean[1];
        final ClientParameters fakeCP = new ClientParameters(trackingId, clientId, applicationName) {
            @Override
            public void resetSession() {
                resetSession[0] = true;
            }

            @Override
            public void setAnonymised(boolean anonymised) {
                setAnonymised[0] = true;
            }
        };
        new GoogleAnalyticsTracker(fakeCP).resetSession();
        Assertions.assertTrue(resetSession[0]);
        new GoogleAnalyticsTracker(fakeCP).setAnonymised(true);
        Assertions.assertTrue(setAnonymised[0]);
    }

    @Test
    public void testProxy() {
        GoogleAnalyticsTracker.setProxy((Proxy) null);
        GoogleAnalyticsTracker.setProxy(Proxy.NO_PROXY);

        // Test various proxy addresses
        // Valid
        Assertions.assertTrue(GoogleAnalyticsTracker.setProxy("http://localhost:80"));
        Assertions.assertTrue(GoogleAnalyticsTracker.setProxy("https://localhost:80"));
        Assertions.assertTrue(GoogleAnalyticsTracker.setProxy("localhost:80"));
        Assertions.assertTrue(GoogleAnalyticsTracker.setProxy("https://localhost:80/more/stuff"));

        // Invalid
        Assertions.assertFalse(GoogleAnalyticsTracker.setProxy((String) null));
        Assertions.assertFalse(GoogleAnalyticsTracker.setProxy(""));
        Assertions.assertFalse(GoogleAnalyticsTracker.setProxy("localhost"));
        Assertions.assertFalse(GoogleAnalyticsTracker.setProxy("http://localhost"));
        Assertions.assertFalse(GoogleAnalyticsTracker.setProxy("http://localhost :80"));

        // Reset
        GoogleAnalyticsTracker.setProxy(Proxy.NO_PROXY);
    }

    @Test
    public void testCompleteBackgroundTasksThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GoogleAnalyticsTracker.completeBackgroundTasks(-1);
        });
    }
}
