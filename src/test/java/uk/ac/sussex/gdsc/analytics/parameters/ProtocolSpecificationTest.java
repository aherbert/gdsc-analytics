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

import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ProtocolSpecificationTest {
  @Test
  public void testIsSupported() {
    // This never gets hit by the code but we should check it works
    Assertions.assertFalse(ProtocolSpecification.PROTOCOL_VERSION.isSupported(null));
    for (final HitType ht : HitType.values()) {
      Assertions.assertTrue(ProtocolSpecification.PROTOCOL_VERSION.isSupported(ht));
      Assertions.assertEquals(ht == HitType.EVENT,
          ProtocolSpecification.EVENT_ACTION.isSupported(ht));
    }
  }

  @Test
  public void testGetMaxLength() {
    // Just check there are different values
    final HashSet<Integer> set = new HashSet<>();
    for (final ProtocolSpecification spec : ProtocolSpecification.values()) {
      final int max = spec.getMaxLength();
      if (spec.getValueType() != ValueType.TEXT) {
        Assertions.assertEquals(0, max);
      }
      set.add(max);
    }
    Assertions.assertTrue(set.size() > 1, "All max lengths are the same");
  }
}
