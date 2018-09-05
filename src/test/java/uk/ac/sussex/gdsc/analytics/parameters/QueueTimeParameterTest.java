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

package uk.ac.sussex.gdsc.analytics.parameters;

import uk.ac.sussex.gdsc.analytics.TestUtils;

import java.util.regex.Pattern;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class QueueTimeParameterTest {
  @Test
  public void testFormat() {
    final long timestamp = System.currentTimeMillis();
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    Pattern pattern = Pattern.compile("^&qt=-?[0-9]+$");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      long stamp = timestamp - rg.nextInt(1000);
      QueueTimeParameter qt = new QueueTimeParameter(stamp);
      String s = TestUtils.callAppendTo(qt);
      Assertions.assertTrue(pattern.matcher(s).matches(), s);
      Assertions.assertEquals(stamp, qt.getValue());
      sb.setLength(0);
      QueueTimeParameter.appendTo(sb, timestamp);
      Assertions.assertTrue(pattern.matcher(sb).matches(), s);
    }
  }
}
