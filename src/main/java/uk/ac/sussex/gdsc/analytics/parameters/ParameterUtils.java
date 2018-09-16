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

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Contains utility functions for checking parameters.
 */
public final class ParameterUtils {

  /**
   * Use to indicate no position in a character array. Set to -1.
   */
  static final int NO_POSITION = -1;

  /**
   * No public construction.
   */
  private ParameterUtils() {
    // Do nothing
  }

  /**
   * Check the string is not empty.
   *
   * @param string the string
   * @param message the message
   * @return the string
   * @throws IllegalArgumentException If the string is empty
   */
  public static String requireNotEmpty(String string, String message) {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }

  /**
   * Check the value is positive.
   *
   * @param value the value
   * @param message the message
   * @return the value
   * @throws IllegalArgumentException If the value is negative
   */
  public static int requirePositive(int value, String message) {
    if (value < 0) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  /**
   * Check the value is positive.
   *
   * @param value the value
   * @param message the message
   * @return the value
   * @throws IllegalArgumentException If the value is negative
   */
  public static long requirePositive(long value, String message) {
    if (value < 0) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  /**
   * Check the value is strictly positive.
   *
   * @param value the value
   * @param message the message
   * @return the value
   * @throws IllegalArgumentException If the value is not strictly positive ({@code >0})
   */
  public static int requireStrictlyPositive(int value, String message) {
    if (value <= 0) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }

  /**
   * Validate the Google Analytics tracking id.
   *
   * <p>The format is UA-XXXX-Y.
   *
   * @param trackingId the tracking id
   * @return the string
   * @throws IllegalArgumentException If the format is not valid
   * @see <a href="http://goo.gl/a8d4RP#tid">Tracking Id</a>
   */
  public static String validateTrackingId(String trackingId) {
    if (!Pattern.matches("[A-Z]{2}-[0-9]{4,}-[0-9]", trackingId)) {
      throw new IllegalArgumentException("Invalid tracking id: " + trackingId);
    }
    return trackingId;
  }

  /**
   * Validate the index count in the specification.
   *
   * @param expected the expected count
   * @param observed the observed
   * @throws IncorrectCountException If the expected and observed do not match
   */
  static void validateCount(int expected, int observed) {
    if (expected != observed) {
      throw new IncorrectCountException(expected, observed);
    }
  }

  /**
   * Validate the index count in the specification.
   *
   * @param expected the expected count
   * @param specification the specification
   * @throws IncorrectCountException If the expected and observed do not match
   */
  static void validateCount(int expected, ParameterSpecification specification) {
    final int observed = specification.getNumberOfIndexes();
    if (expected != observed) {
      throw new IncorrectCountException(expected, observed, specification.getFormalName());
    }
  }

  /**
   * Validate the value types are compatible. This is used to ensure that the {@code value} appended
   * to a {@code name=value} pair is compatible.
   *
   * <p>Note that if the expected type is text then this method does not throw, i.e. it checks for
   * compatible value types.
   *
   * @param expected the expected value type
   * @param specification the specification
   * @throws IncorrectValueTypeException If the expected and observed do not match
   */
  static void compatibleValueType(ValueType expected, ParameterSpecification specification) {
    // Text is compatible with any other type
    if (expected != ValueType.TEXT) {
      final ValueType observed = specification.getValueType();
      if (expected != observed) {
        throw new IncorrectValueTypeException(expected, observed, specification.getFormalName());
      }
    }
  }

  /**
   * Find the next position of the underscore character in the value, starting from the given index.
   *
   * @param value the value
   * @param fromIndex the from index (should be positive)
   * @return the position (or {@link #NO_POSITION} if not found)
   * @see Constants#UNDERSCORE
   */
  static int nextUnderscore(char[] value, int fromIndex) {
    final int max = value.length;
    for (int i = fromIndex; i < max; i++) {
      if (value[i] == Constants.UNDERSCORE) {
        return i;
      }
    }
    return NO_POSITION;
  }

  /**
   * Gets the chars from the {@link StringBuilder}.
   *
   * @param sb the string builder (must not be null)
   * @return the chars
   */
  public static char[] getChars(StringBuilder sb) {
    final char[] chars = new char[sb.length()];
    sb.getChars(0, sb.length(), chars, 0);
    return chars;
  }

  /**
   * Gets the chars from the {@link CharSequence}.
   *
   * <p>If the argument is {@code] null} then an empty array is returned.
   *
   * @param sequence the sequence
   * @return the chars
   */
  public static char[] getChars(CharSequence sequence) {
    if (sequence == null) {
      return Constants.EMPTY_CHARS;
    }
    // All CharSequences implement toString() to return
    // the String containing the sequence
    return sequence.toString().toCharArray();
  }

  /**
   * Count the number of indexes in the name format.
   *
   * <p>Indexes use the {@code _} (underscore) character.
   *
   * <p>This assumes that all the characters are below
   * {@link Character#MIN_SUPPLEMENTARY_CODE_POINT}, i.e. this is not a string with supplementary
   * characters.
   *
   * @param nameFormat the name format
   * @return the count
   * @see Constants#UNDERSCORE
   */
  public static int countIndexes(CharSequence nameFormat) {
    int count = 0;
    if (nameFormat != null) {
      for (int i = nameFormat.length(); i-- > 0;) {
        if (nameFormat.charAt(i) == Constants.UNDERSCORE) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Append the number value to the {@link StringBuilder}.
   *
   * <p>If the number has an integer representation then this is used instead.
   *
   * @param sb the string builder
   * @param value the value
   * @return the string builder
   */
  public static StringBuilder appendNumberTo(StringBuilder sb, double value) {
    final double floor = Math.floor(value);
    if (floor == value) {
      sb.append((long) floor);
    } else {
      sb.append(value);
    }
    return sb;
  }

  /**
   * Append the currency value to the {@link StringBuilder}.
   *
   * <p>Current the format expected for Google Analytics current is unknown. This uses
   * {@link NumberFormat#getCurrencyInstance()}.
   *
   * @param sb the string builder
   * @param locale the locale
   * @param value the value
   * @return the string builder
   */
  public static StringBuilder appendCurrencyTo(StringBuilder sb, Locale locale, double value) {
    final NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
    // It is unclear what to pass for the field position when appending to a
    // StringBuilder. The formatter uses package level classes that do nothing
    // if you pass them to the format(double) method:
    // Just call the default to get a string then copy the output.
    return sb.append(formatter.format(value));
  }

  /**
   * Checks if the string is not empty.
   *
   * @param string the string
   * @return true, if is not empty
   */
  public static boolean isNotEmpty(String string) {
    return string != null && string.length() > 0;
  }
}
