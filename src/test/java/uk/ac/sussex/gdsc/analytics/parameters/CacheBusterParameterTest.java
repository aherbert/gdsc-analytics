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

package uk.ac.sussex.gdsc.analytics.parameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.sussex.gdsc.analytics.TestUtils;

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
    final ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      list.add(TestUtils.randomName(rg, 3));
    }
    // This does not have to be thread safe but works anyway
    final AtomicInteger counter = new AtomicInteger();
    final CacheBusterParameter cb = new CacheBusterParameter((sb) -> {
      return sb.append(list.get(counter.getAndIncrement()));
    });
    for (int i = 0; i < list.size(); i++) {
      final String s = cb.format();
      Assertions.assertEquals("z=" + list.get(i), s);
    }
  }

  @Test
  public void testGetDefaultInstance() {
    final CacheBusterParameter cb = CacheBusterParameter.getDefaultInstance();
    final Pattern pattern = Pattern.compile("^z=-?[0-9]+$");
    final HashSet<String> set = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      final String s = cb.format();
      Assertions.assertTrue(pattern.matcher(s).matches(), s);
      set.add(s);
    }
    Assertions.assertTrue(set.size() > 1, "Not a random cache buster");
  }
}
