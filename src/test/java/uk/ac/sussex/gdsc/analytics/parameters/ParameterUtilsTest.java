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
import java.util.Locale;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ParameterUtilsTest {

  private static String message;

  @BeforeAll
  public static void setup() {
    UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    message = TestUtils.randomName(rg, 30);
  }

  @AfterAll
  public static void teardown() {
    message = null;
  }

  @Test
  public void testRequireNotEmpty() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireNotEmpty(null, message);
    }).getMessage());
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireNotEmpty("", message);
    }).getMessage());
  }

  @Test
  public void testRequirePositive() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requirePositive(-1, message);
    }).getMessage());
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requirePositive(-1L, message);
    }).getMessage());
    for (int i = 0; i < 5; i++) {
      Assertions.assertEquals(i, ParameterUtils.requirePositive(i, message));
      Assertions.assertEquals(i, ParameterUtils.requirePositive((long) i, message));
    }
  }

  @Test
  public void testRequireStrictlyPositive() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireStrictlyPositive(0, message);
    }).getMessage());
    for (int i = 1; i < 5; i++) {
      Assertions.assertEquals(i, ParameterUtils.requireStrictlyPositive(i, message));
    }
  }

  @Test
  public void testValidateTrackingId() {
    String good = "UA-1234-5";
    ParameterUtils.validateTrackingId(good);

    Assertions.assertThrows(NullPointerException.class, () -> {
      ParameterUtils.validateTrackingId(null);
    });

    for (String trackingId : new String[] {
        // Just bad
        "bad", "asdfasdf",
        // Empty
        "",
        // Wrong format
        "UAA-A-0", "UDHDHDH", "00-123-5",
        // Bad case
        good.toLowerCase(),
        // whitespace
        " " + good, good + " ",
        // Extra digits
        "U" + good, good + "9"}) {
      Assertions.assertThrows(IllegalArgumentException.class, () -> {
        ParameterUtils.validateTrackingId(trackingId);
      });
    }

    for (String trackingId : new String[] {"UA-1234-5", "UA-1234567-0"}) {
      ParameterUtils.validateTrackingId(trackingId);
    }
  }

  @Test
  public void testValidateCount() {
    // Test different sizes
    ArrayList<ProtocolSpecification> list = new ArrayList<>();
    list.add(ProtocolSpecification.PROTOCOL_VERSION); // 0
    list.add(ProtocolSpecification.CUSTOM_DIMENSION); // 1
    list.add(ProtocolSpecification.PRODUCT_CUSTOM_DIMENSION); // 2
    list.add(ProtocolSpecification.PRODUCT_IMPRESSION_CUSTOM_DIMENSION); // 3
    for (ProtocolSpecification spec : list) {
      final int expected = spec.getNumberOfIndexes() + 1;
      IncorrectCountException e = Assertions.assertThrows(IncorrectCountException.class, () -> {
        ParameterUtils.validateCount(expected, spec);
      });
      Assertions.assertEquals(expected, e.getExpected());
      Assertions.assertEquals(spec.getNumberOfIndexes(), e.getObserved());

    }
    for (ProtocolSpecification spec : ProtocolSpecification.values()) {
      ParameterUtils.validateCount(spec.getNumberOfIndexes(), spec);
    }
  }

  @Test
  public void testCompatibleValueType() {
    // Test different types
    ArrayList<ProtocolSpecification> list = new ArrayList<>();
    list.add(ProtocolSpecification.CUSTOM_DIMENSION); // Text
    list.add(ProtocolSpecification.QUEUE_TIME); // Integer
    list.add(ProtocolSpecification.CUSTOM_METRIC); // Number
    list.add(ProtocolSpecification.PRODUCT_PRICE); // Currency
    list.add(ProtocolSpecification.IS_EXCEPTION_FATAL); // Boolean
    // All OK with text
    for (ProtocolSpecification spec : list) {
      ParameterUtils.compatibleValueType(ValueType.TEXT, spec);
    }
    // For now the others must be an exact match
    for (int i = 1; i < list.size(); i++) {
      final ValueType expected = list.get(i).getValueType();
      for (int j = 0; j < list.size(); j++) {
        final ProtocolSpecification spec2 = list.get(j);
        if (i != j) {
          IncorrectValueTypeException e =
              Assertions.assertThrows(IncorrectValueTypeException.class, () -> {
                ParameterUtils.compatibleValueType(expected, spec2);
              });
          Assertions.assertEquals(expected, e.getExpected());
          Assertions.assertEquals(spec2.getValueType(), e.getObserved());
        }
      }
    }
  }

  @Test
  public void testNextUnderscore() {
    // Edge cases
    testNextUnderscore("");
    testNextUnderscore("none");
    testNextUnderscore("_leading");
    testNextUnderscore("trailing_");
    testNextUnderscore("inter_nal");
    // Do all the format strings
    for (ProtocolSpecification spec : ProtocolSpecification.values()) {
      testNextUnderscore(spec.getNameFormat().toString());
    }
  }

  private static void testNextUnderscore(String string) {
    char[] chars = string.toCharArray();
    // Should function like indexOf
    int fromIndex = 0;
    int expected = string.indexOf(Constants.UNDERSCORE, fromIndex);
    int actual = ParameterUtils.nextUnderscore(chars, fromIndex);
    Assertions.assertEquals(expected, actual, string);
    while (expected != -1) {
      fromIndex = expected + 1;
      expected = string.indexOf(Constants.UNDERSCORE, fromIndex);
      actual = ParameterUtils.nextUnderscore(chars, fromIndex);
      Assertions.assertEquals(expected, actual, string);
    }
  }

  @Test
  public void testGetChars() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      String name = TestUtils.randomName(rg, 5);
      StringBuilder sb = new StringBuilder(name);
      char[] expected = name.toCharArray();
      Assertions.assertArrayEquals(expected, ParameterUtils.getChars(name));
      Assertions.assertArrayEquals(expected, ParameterUtils.getChars(sb));
    }
  }

  @Test
  public void testCountIndexes() {
    // Edge cases
    Assertions.assertEquals(0, ParameterUtils.countIndexes(null), "null");
    testCountIndexes("");
    testCountIndexes("none");
    testCountIndexes("_leading");
    testCountIndexes("trailing_");
    testCountIndexes("inter_nal");
    // Do all the format strings
    for (ProtocolSpecification spec : ProtocolSpecification.values()) {
      testCountIndexes(spec.getNameFormat());
    }
  }

  private static void testCountIndexes(CharSequence format) {
    String string = format.toString();
    int expected = 0;
    int index = string.indexOf(Constants.UNDERSCORE);
    while (index != -1) {
      expected++;
      index = string.indexOf(Constants.UNDERSCORE, index + 1);
    }
    Assertions.assertEquals(expected, ParameterUtils.countIndexes(format), string);
  }

  @Test
  public void testAppendNumberTo() {
    // Negative integer
    Assertions.assertEquals("-1",
        ParameterUtils.appendNumberTo(new StringBuilder(), -1.0).toString());
    // Negative fraction with limited precision
    Assertions.assertEquals("-1.5",
        ParameterUtils.appendNumberTo(new StringBuilder(), -1.5).toString());
    // Zero
    Assertions.assertEquals("0",
        ParameterUtils.appendNumberTo(new StringBuilder(), 0.0).toString());
    // Positive fraction with limit precision
    Assertions.assertEquals("1.5",
        ParameterUtils.appendNumberTo(new StringBuilder(), 1.5).toString());
    Assertions.assertEquals("2",
        ParameterUtils.appendNumberTo(new StringBuilder(), 2.0).toString());
    // No rounding of irrational number
    Assertions.assertEquals(Double.toString(Math.PI),
        ParameterUtils.appendNumberTo(new StringBuilder(), Math.PI).toString());
  }

  @Test
  public void testACurrencyTo() {
    Locale locale = Locale.UK;
    // Add pence when not present
    Assertions.assertEquals("£0.00",
        ParameterUtils.appendCurrencyTo(new StringBuilder(), locale, 0).toString());
    // Rounding
    Assertions.assertEquals("£1.99",
        ParameterUtils.appendCurrencyTo(new StringBuilder(), locale, 1.99).toString());
    Assertions.assertEquals("£2.00",
        ParameterUtils.appendCurrencyTo(new StringBuilder(), locale, 1.995).toString());
    // Negatives
    Assertions.assertEquals("-£1.99",
        ParameterUtils.appendCurrencyTo(new StringBuilder(), locale, -1.99).toString());
    // Rounding to the locale
    Assertions.assertEquals("£3.14",
        ParameterUtils.appendCurrencyTo(new StringBuilder(), locale, Math.PI).toString());
  }

  @Test
  public void testIsNotEmpty() {
    Assertions.assertFalse(ParameterUtils.isNotEmpty(null));
    Assertions.assertFalse(ParameterUtils.isNotEmpty(""));
    Assertions.assertTrue(ParameterUtils.isNotEmpty("h"));
    Assertions.assertTrue(ParameterUtils.isNotEmpty("hello"));
  }
}
