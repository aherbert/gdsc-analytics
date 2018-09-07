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

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ProtocolSpecificationTest {
  @Test
  public void testIsSupported() {
    // This never gets hit by the code but we should check it works
    Assertions.assertFalse(ProtocolSpecification.PROTOCOL_VERSION.isSupported(null));
    for (HitType ht : HitType.values()) {
      Assertions.assertTrue(ProtocolSpecification.PROTOCOL_VERSION.isSupported(ht));
      Assertions.assertEquals(ht == HitType.EVENT,
          ProtocolSpecification.EVENT_ACTION.isSupported(ht));
    }
  }

  @Test
  public void testGetMaxLength() {
    // Just check there are different values
    HashSet<Integer> set = new HashSet<>();
    for (ProtocolSpecification spec : ProtocolSpecification.values()) {
      int max = spec.getMaxLength();
      if (spec.getValueType() != ValueType.TEXT) {
        Assertions.assertEquals(0, max);
      }
      set.add(max);
    }
    Assertions.assertTrue(set.size() > 1, "All max lengths are the same");
  }
}
