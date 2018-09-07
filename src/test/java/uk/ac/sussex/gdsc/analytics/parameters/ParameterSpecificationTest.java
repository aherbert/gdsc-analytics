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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ParameterSpecificationTest {
  @Test
  public void testDefaultMethods() {
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
    Assertions.assertEquals(0, spec.getSupportedHitTypes().length);
    Assertions.assertEquals(1, spec.getNumberOfIndexes());
    // Don't support null
    Assertions.assertEquals(false, spec.isSupported(null));
    // Support all
    for (HitType ht : HitType.values()) {
      Assertions.assertTrue(spec.isSupported(ht));
    }

    HitType[] supported = new HitType[] {HitType.EVENT};
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
      public HitType[] getSupportedHitTypes() {
        return supported;
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertEquals(0, spec.getMaxLength());
    Assertions.assertArrayEquals(supported, spec.getSupportedHitTypes());
    Assertions.assertEquals(2, spec.getNumberOfIndexes());
    Assertions.assertEquals(false, spec.isSupported(null));
    for (HitType ht : HitType.values()) {
      Assertions.assertEquals(ht == HitType.EVENT, spec.isSupported(ht));
    }

    HitType[] supported2 = new HitType[] {HitType.EVENT, HitType.ITEM};
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
      public HitType[] getSupportedHitTypes() {
        return supported2;
      }

      @Override
      public ValueType getValueType() {
        return null;
      }
    };
    Assertions.assertArrayEquals(supported2, spec.getSupportedHitTypes());
    Assertions.assertEquals(false, spec.isSupported(null));
    for (HitType ht : HitType.values()) {
      Assertions.assertEquals(ht == HitType.EVENT || ht == HitType.ITEM, spec.isSupported(ht));
    }
  }
}
