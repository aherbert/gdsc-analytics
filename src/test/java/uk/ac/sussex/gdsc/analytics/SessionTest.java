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
public class SessionTest {
  @Test
  public void testReset() {
    final Session session = new Session();
    Assertions.assertTrue(session.isNew());
    Assertions.assertFalse(session.isNew());
    session.reset();
    Assertions.assertTrue(session.isNew());
  }

  @Test
  public void testTimeout() {
    final Session session = new Session();
    // 1 second timeout
    session.setTimeout(1);
    Assertions.assertEquals(1, session.getTimeout());
    Assertions.assertTrue(session.isNew());
    Assertions.assertFalse(session.isNew());
    try {
      // Sleep for 1 second to ensure timeout
      Thread.sleep(1000);
    } catch (final InterruptedException ex) { // Ignore
    }
    Assertions.assertTrue(session.isNew());
  }
}
