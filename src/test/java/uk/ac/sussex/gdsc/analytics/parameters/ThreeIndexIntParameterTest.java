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

import uk.ac.sussex.gdsc.analytics.TestUtils;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ThreeIndexIntParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    final ParameterSpecification spec = TestUtils.newIntParameterSpecification("test_test_test_");
    final int index1 = 1;
    final int index2 = 2;
    final int index3 = 3;
    final int value = 4;
    Assertions.assertThrows(NullPointerException.class, () -> {
      new ThreeIndexIntParameter((ParameterSpecification) null, index1, index2, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec, 0, index2, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec, index1, 0, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec, index1, index2, 0, value);
    });

    final ProtocolSpecification spec2 = ProtocolSpecification.PRODUCT_IMPRESSION_CUSTOM_METRIC;
    Assertions.assertThrows(NullPointerException.class, () -> {
      new ThreeIndexIntParameter((ProtocolSpecification) null, index1, index2, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec2, 0, index2, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec2, index1, 0, index3, value);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ThreeIndexIntParameter(spec2, index1, index2, 0, value);
    });
  }

  @Test
  public void testFormat() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name1 = TestUtils.randomName(rg, 3);
      final String name2 = TestUtils.randomName(rg, 3);
      final String name3 = TestUtils.randomName(rg, 3);
      final int index1 = 1 + rg.nextInt(20);
      final int index2 = 1 + rg.nextInt(20);
      final int index3 = 1 + rg.nextInt(20);
      final int value = rg.nextInt();
      ThreeIndexIntParameter param = new ThreeIndexIntParameter(
          TestUtils.newIntParameterSpecification(name1 + "_" + name2 + "_" + name3 + "_"), index1,
          index2, index3, value);
      Assertions.assertEquals(
          String.format("%s%d%s%d%s%d=%d", name1, index1, name2, index2, name3, index3, value),
          param.format());
      // Repeat as this checks the cache version is the same
      Assertions.assertEquals(
          String.format("%s%d%s%d%s%d=%d", name1, index1, name2, index2, name3, index3, value),
          param.format());
      Assertions.assertEquals(index1, param.getIndex1());
      Assertions.assertEquals(index2, param.getIndex2());
      Assertions.assertEquals(index3, param.getIndex3());
      Assertions.assertEquals(value, param.getValue());

      param = new ThreeIndexIntParameter(ProtocolSpecification.PRODUCT_IMPRESSION_CUSTOM_METRIC,
          index1, index2, index3, value);
      Assertions.assertEquals(String.format("il%dpi%dcm%d=%s", index1, index2, index3, value),
          param.format());
      Assertions.assertEquals(value, param.getValue());
    }
  }
}
