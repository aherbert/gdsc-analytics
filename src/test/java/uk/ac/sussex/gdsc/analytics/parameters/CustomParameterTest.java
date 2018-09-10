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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class CustomParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    String name = "name";
    String value = "value";
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CustomParameter(null, value);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CustomParameter(name, null);
    });
  }

  @Test
  public void testFormat() throws UnsupportedEncodingException {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String name = TestUtils.randomPath(rg, 5);
      String value = TestUtils.randomPath(rg, 5);
      CustomParameter cp = new CustomParameter(name, value);
      Assertions.assertEquals(String.format("%s=%s", URLEncoder.encode(name, "utf-8"),
          URLEncoder.encode(value, "utf-8")), cp.format());
      Assertions.assertEquals(name, cp.getName());
      Assertions.assertEquals(value, cp.getValue());
    }
  }
}
