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
public class OneIndexTextParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    ParameterSpecification spec = TestUtils.newTextParameterSpecification("test_", 0);
    int index = 1;
    String value = "test";
    Assertions.assertThrows(NullPointerException.class, () -> {
      new OneIndexTextParameter(spec, index, null);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new OneIndexTextParameter(null, 1, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new OneIndexTextParameter(spec, 0, value);
    });
  }

  @Test
  public void testFormat() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String name = TestUtils.randomName(rg, 3);
      int index = 1 + rg.nextInt(20);
      String value = TestUtils.randomName(rg, 3);
      OneIndexTextParameter param = new OneIndexTextParameter(
          TestUtils.newTextParameterSpecification(name + "_", 0), index, value);
      Assertions.assertEquals(String.format("%s%d=%s", name, index, value), param.format());
      // Repeat as this checks the cache version is the same
      Assertions.assertEquals(String.format("%s%d=%s", name, index, value), param.format());
      Assertions.assertEquals(index, param.getIndex());
      Assertions.assertEquals(value, param.getValue());

      param = new OneIndexTextParameter(ProtocolSpecification.CUSTOM_DIMENSION, index, value);
      Assertions.assertEquals(String.format("cd%d=%s", index, value), param.format());
      Assertions.assertEquals(value, param.getValue());
    }
  }
}