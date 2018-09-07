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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class CacheBusterParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CacheBusterParameter(null);
    });
  }


  @Test
  public void testFormat() {
    // Create random strings to append
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(TestUtils.randomName(rg, 3));
    }
    // This does not have to be thread safe but works anyway
    final AtomicInteger counter = new AtomicInteger();
    CacheBusterParameter cb = new CacheBusterParameter((sb) -> {
      return sb.append(list.get(counter.getAndIncrement()));
    });
    for (int i = 0; i < list.size(); i++) {
      String s = cb.format();
      Assertions.assertEquals("z=" + list.get(i), s);
    }
  }

  @Test
  public void testGetDefaultInstance() {
    CacheBusterParameter cb = CacheBusterParameter.getDefaultInstance();
    Pattern pattern = Pattern.compile("^z=-?[0-9]+$");
    HashSet<String> set = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      String s = cb.format();
      Assertions.assertTrue(pattern.matcher(s).matches(), s);
      set.add(s);
    }
    Assertions.assertTrue(set.size() > 1, "Not a random cache buster");
  }
}
