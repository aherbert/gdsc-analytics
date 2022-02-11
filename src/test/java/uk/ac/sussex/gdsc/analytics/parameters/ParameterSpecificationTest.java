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

import java.util.EnumSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
class ParameterSpecificationTest {
  @Test
  void testDefaultMethods() {
    ParameterSpecification spec = new ParameterSpecification() {

      @Override
      public String getFormalName() {
        return null;
      }

      @Override
      public CharSequence getNameFormat() {
        return "test_1";
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertEquals(0, spec.getMaxLength());
    Assertions.assertNotNull(spec.getSupportedHitTypes());
    Assertions.assertEquals(EnumSet.allOf(HitType.class), spec.getSupportedHitTypes());
    Assertions.assertEquals(1, spec.getNumberOfIndexes());
    // Don't support null
    Assertions.assertEquals(false, spec.isSupported(null));
    // Support all
    for (final HitType ht : HitType.values()) {
      Assertions.assertTrue(spec.isSupported(ht));
    }

    final EnumSet<HitType> supported = EnumSet.of(HitType.EVENT);
    spec = new ParameterSpecification() {

      @Override
      public String getFormalName() {
        return null;
      }

      @Override
      public CharSequence getNameFormat() {
        return "test_1and_2";
      }

      @Override
      public EnumSet<HitType> getSupportedHitTypes() {
        return supported;
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertEquals(0, spec.getMaxLength());
    Assertions.assertEquals(supported, spec.getSupportedHitTypes());
    Assertions.assertEquals(2, spec.getNumberOfIndexes());
    Assertions.assertEquals(false, spec.isSupported(null));
    for (final HitType ht : HitType.values()) {
      Assertions.assertEquals(ht == HitType.EVENT, spec.isSupported(ht));
    }

    final EnumSet<HitType> supported2 = EnumSet.of(HitType.EVENT, HitType.ITEM);
    spec = new ParameterSpecification() {

      @Override
      public String getFormalName() {
        return null;
      }

      @Override
      public CharSequence getNameFormat() {
        return "test_1and_2";
      }

      @Override
      public EnumSet<HitType> getSupportedHitTypes() {
        return supported2;
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertEquals(supported2, spec.getSupportedHitTypes());
    Assertions.assertEquals(false, spec.isSupported(null));
    for (final HitType ht : HitType.values()) {
      Assertions.assertEquals(ht == HitType.EVENT || ht == HitType.ITEM, spec.isSupported(ht));
    }

    spec = new ParameterSpecification() {

      @Override
      public String getFormalName() {
        return null;
      }

      @Override
      public CharSequence getNameFormat() {
        return "test_1and_2";
      }

      @Override
      public EnumSet<HitType> getSupportedHitTypes() {
        // Explicitly return null
        return null;
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertNull(spec.getSupportedHitTypes());
    // Don't support null
    Assertions.assertEquals(false, spec.isSupported(null));
    // Support all
    for (final HitType ht : HitType.values()) {
      Assertions.assertTrue(spec.isSupported(ht));
    }
  }
}
