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

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class SessionTest {
  @Test
  public void testReset() {
    final Session session = new Session();
    Assertions.assertTrue(session.refresh());
    Assertions.assertFalse(session.refresh());
    session.reset();
    Assertions.assertTrue(session.refresh());
  }

  @SuppressWarnings("unused")
  @Test
  public void testTimeout() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new Session(-1);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new Session().setTimeout(-1);
    });

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      long timeout = rg.nextLong(1000000L);
      Session session = new Session(timeout);
      Assertions.assertEquals(0, session.getTimeStamp());
      Assertions.assertEquals(timeout, session.getTimeout());
      session.refresh();
      Assertions.assertFalse(0L == session.getTimeStamp(), "Timestamp has not changed");
    }

    // 0 millisecond timeout
    Session session = new Session(Session.ALWAYS_TIMEOUT);
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());

    // Never timeout. This should work on the same session
    //session = new Session(Session.NEVER_TIMEOUT);
    session.setTimeout(Session.NEVER_TIMEOUT);
    Assertions.assertEquals(Long.MAX_VALUE, session.getTimeout());
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertFalse(session.refresh());
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertFalse(session.refresh());
    session.reset();
    Assertions.assertTrue(session.refresh());
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertFalse(session.refresh());
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertFalse(session.refresh());
  }
}
