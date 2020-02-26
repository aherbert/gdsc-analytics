/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2020 Alex Herbert
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
      final long timeout = rg.nextLong(1000000L);
      final Session session = new Session(timeout);
      Assertions.assertEquals(0, session.getTimeStamp());
      Assertions.assertEquals(timeout, session.getTimeout());
      session.refresh();
      Assertions.assertFalse(0L == session.getTimeStamp(), "Timestamp has not changed");
    }

    // 0 millisecond timeout
    final Session session = new Session(Session.ALWAYS_TIMEOUT);
    Assertions.assertFalse(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());
    Assertions.assertTrue(session.refresh());
    Assertions.assertTrue(session.hasExpired());

    // Never timeout. This should work on the same session
    // session = new Session(Session.NEVER_TIMEOUT);
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
