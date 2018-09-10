/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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
public class CustomParameterSpecificationTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    String formalName = "formalName";
    String nameFormat = "name";
    ValueType valueType = ValueType.TEXT;
    int maxLength = 0;
    new CustomParameterSpecification(formalName, nameFormat, valueType, maxLength);
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CustomParameterSpecification(null, nameFormat, valueType, maxLength);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CustomParameterSpecification(formalName, null, valueType, maxLength);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new CustomParameterSpecification(formalName, nameFormat, null, maxLength);
    });
  }

  @Test
  public void testProperties() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (ValueType valueType : ValueType.values()) {
      String formalName = TestUtils.randomName(rg, 3);
      String nameFormat = TestUtils.randomName(rg, 3);
      int maxLength = rg.nextInt(100);
      CustomParameterSpecification spec =
          new CustomParameterSpecification(formalName, nameFormat, valueType, maxLength);
      Assertions.assertEquals(formalName, spec.getFormalName());
      Assertions.assertEquals(nameFormat, spec.getNameFormat());
      Assertions.assertEquals(valueType, spec.getValueType());
      Assertions.assertEquals(maxLength, spec.getMaxLength());
    }

    CustomParameterSpecification spec = new CustomParameterSpecification("", "", ValueType.TEXT, 0,
        HitType.EVENT, HitType.EXCEPTION);
    Assertions.assertArrayEquals(new HitType[] {HitType.EVENT, HitType.EXCEPTION},
        spec.getSupportedHitTypes());
  }
}
