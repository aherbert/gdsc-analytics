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
