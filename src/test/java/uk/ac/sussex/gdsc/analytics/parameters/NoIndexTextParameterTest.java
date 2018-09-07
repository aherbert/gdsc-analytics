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

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class NoIndexTextParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexTextParameter(TestUtils.newTextParameterSpecification("Test"), null);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexTextParameter(null, "Value");
    });
  }

  @Test
  public void testFormat() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String name = TestUtils.randomName(rg, 3);
      String value = TestUtils.randomName(rg, 3);
      NoIndexTextParameter param =
          new NoIndexTextParameter(TestUtils.newTextParameterSpecification(name), value);
      Assertions.assertEquals(String.format("%s=%s", name, value), param.format());
      Assertions.assertEquals(value, param.getValue());

      param = new NoIndexTextParameter(ProtocolSpecification.DATA_SOURCE, value);
      Assertions.assertEquals(String.format("ds=%s", value), param.format());
      Assertions.assertEquals(value, param.getValue());
    }
  }
}
