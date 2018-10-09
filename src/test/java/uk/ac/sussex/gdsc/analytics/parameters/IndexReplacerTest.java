/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@SuppressWarnings("javadoc")
public class IndexReplacerTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new IndexReplacer((ProtocolSpecification) null);
    });

    // Handle null char sequence
    final IndexReplacer replacer = new IndexReplacer((CharSequence) null);
    Assertions.assertEquals(0, replacer.getNumberOfIndexes());
    Assertions.assertEquals("", replacer.getFormat());
  }

  @Test
  public void testReplaceTo() {
    testReplaceTo("none", "none");
    testReplaceTo("one_", "one5", 5);
    testReplaceTo("_one", "6one", 6);
    testReplaceTo("o_ne", "o7ne", 7);
    testReplaceTo("one_two_", "one5two88", 5, 88);
    testReplaceTo("_one_two", "6one99two", 6, 99);
    testReplaceTo("o_net_wo", "o7net100wo", 7, 100);
    testReplaceTo("one_two_three_", "one5two88three114", 5, 88, 114);
    testReplaceTo("_one_two_three", "6one99two115three", 6, 99, 115);
    testReplaceTo("o_net_wothr_ee", "o7net100wothr116ee", 7, 100, 116);

    IncorrectCountException ex = Assertions.assertThrows(IncorrectCountException.class, () -> {
      final IndexReplacer replacer = new IndexReplacer("none");
      replacer.replaceTo(new StringBuilder(), 1);
    });
    Assertions.assertEquals(0, ex.getExpected());
    Assertions.assertEquals(1, ex.getObserved());

    ex = Assertions.assertThrows(IncorrectCountException.class, () -> {
      final IndexReplacer replacer = new IndexReplacer("one_");
      replacer.replaceTo(new StringBuilder());
    });
    Assertions.assertEquals(1, ex.getExpected());
    Assertions.assertEquals(0, ex.getObserved());

    ex = Assertions.assertThrows(IncorrectCountException.class, () -> {
      final IndexReplacer replacer = new IndexReplacer("one_");
      replacer.replaceTo(new StringBuilder(), 1, 2);
    });
    Assertions.assertEquals(1, ex.getExpected());
    Assertions.assertEquals(2, ex.getObserved());
  }

  private static void testReplaceTo(String format, String expected, int... indexes) {
    final IndexReplacer replacer = new IndexReplacer(format);
    Assertions.assertEquals(indexes.length, replacer.getNumberOfIndexes());
    Assertions.assertEquals(format, replacer.getFormat());
    Assertions.assertEquals(expected, replacer.replaceTo(new StringBuilder(), indexes).toString());
  }

  @SuppressWarnings("unused")
  @Test
  public void testXIndexReplacerThrows() {
    // Test different sizes
    final ArrayList<ProtocolSpecification> list = new ArrayList<>();
    list.add(ProtocolSpecification.PROTOCOL_VERSION); // 0
    list.add(ProtocolSpecification.CUSTOM_DIMENSION); // 1
    list.add(ProtocolSpecification.PRODUCT_CUSTOM_DIMENSION); // 2
    list.add(ProtocolSpecification.PRODUCT_IMPRESSION_CUSTOM_DIMENSION); // 3
    for (final ProtocolSpecification spec : list) {
      if (spec.getNumberOfIndexes() != 0) {
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new NoIndexReplacer(spec);
        });
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new NoIndexReplacer(spec.getNameFormat());
        });
      }
      if (spec.getNumberOfIndexes() != 1) {
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new OneIndexReplacer(spec);
        });
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new OneIndexReplacer(spec.getNameFormat());
        });
      }
      if (spec.getNumberOfIndexes() != 2) {
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new TwoIndexReplacer(spec);
        });
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new TwoIndexReplacer(spec.getNameFormat());
        });
      }
      if (spec.getNumberOfIndexes() != 3) {
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new ThreeIndexReplacer(spec);
        });
        Assertions.assertThrows(IncorrectCountException.class, () -> {
          new ThreeIndexReplacer(spec.getNameFormat());
        });
      }
    }
  }

  @Test
  public void testIndexReplacerFactory() {
    for (final ProtocolSpecification spec : ProtocolSpecification.values()) {
      final IndexReplacer r = IndexReplacerFactory.createIndexReplacer(spec);
      switch (spec.getNumberOfIndexes()) {
        case 0:
          Assertions.assertEquals(NoIndexReplacer.class, r.getClass());
          break;
        case 1:
          Assertions.assertEquals(OneIndexReplacer.class, r.getClass());
          break;
        case 2:
          Assertions.assertEquals(TwoIndexReplacer.class, r.getClass());
          break;
        case 3:
          Assertions.assertEquals(ThreeIndexReplacer.class, r.getClass());
          break;
        default:
          Assertions.fail("No specialised replacer for indexes: " + spec.getNumberOfIndexes());
      }
      Assertions.assertSame(r, IndexReplacerFactory.createIndexReplacer(spec));
    }
  }

  @Test
  public void testXIndexReplacer() {
    // Test different sizes
    NoIndexReplacer r0 = (NoIndexReplacer) IndexReplacerFactory
        .createIndexReplacer(ProtocolSpecification.PROTOCOL_VERSION);
    Assertions.assertEquals("v", r0.replaceTo(new StringBuilder()).toString());
    r0 = new NoIndexReplacer("none");
    Assertions.assertEquals("none", r0.replaceTo(new StringBuilder()).toString());

    OneIndexReplacer r1 = (OneIndexReplacer) IndexReplacerFactory
        .createIndexReplacer(ProtocolSpecification.CUSTOM_DIMENSION);
    Assertions.assertEquals("cd4", r1.replaceTo(new StringBuilder(), 4).toString());
    r1 = new OneIndexReplacer("one_xx");
    Assertions.assertEquals("one97xx", r1.replaceTo(new StringBuilder(), 97).toString());

    TwoIndexReplacer r2 = (TwoIndexReplacer) IndexReplacerFactory
        .createIndexReplacer(ProtocolSpecification.PRODUCT_CUSTOM_DIMENSION);
    Assertions.assertEquals("pr5cd6", r2.replaceTo(new StringBuilder(), 5, 6).toString());
    r2 = new TwoIndexReplacer("one_two_xx");
    Assertions.assertEquals("one97two98xx", r2.replaceTo(new StringBuilder(), 97, 98).toString());

    ThreeIndexReplacer r3 = (ThreeIndexReplacer) IndexReplacerFactory
        .createIndexReplacer(ProtocolSpecification.PRODUCT_IMPRESSION_CUSTOM_DIMENSION);
    Assertions.assertEquals("il7pi8cd9", r3.replaceTo(new StringBuilder(), 7, 8, 9).toString());
    r3 = new ThreeIndexReplacer("one_two_three_xx");
    Assertions.assertEquals("one97two98three99xx",
        r3.replaceTo(new StringBuilder(), 97, 98, 99).toString());
  }
}
