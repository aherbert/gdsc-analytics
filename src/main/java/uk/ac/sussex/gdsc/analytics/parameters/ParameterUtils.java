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

import java.util.regex.Pattern;

/**
 * Contains utility functions for checking parameters.
 */
public final class ParameterUtils {

  /** The '<strong>{@code =}</strong>' (Equal) character. */
  public static final char EQUAL = '=';

  /** The '<strong>{@code &}</strong>' (Ampersand) character. */
  public static final char AND = '&';

  /**
   * The character used to identify an index within the name format for the {@code name=value}
   * parameter pair.
   */
  public static final char INDEX_CHARACTER = Parameter.INDEX_CHARACTER;

  /**
   * Use to indicate no position in a character array. Set to -1.
   */
  public static final int NO_POSITION = -1;

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
   * Validate the count.
   *
   * @param expected the expected count
   * @param observed the observed count
   * @throws IncorrectCountException If the expected and observed do not match
   */
  public static void validateCount(int expected, int observed) {
    if (expected != observed) {
      throw new IncorrectCountException(expected, observed);
    }
  }

  /**
   * Validate the value types are compatible. This is used to ensure that the {@code value} appended
   * to a {@code name=value} pair is compatible.
   * 
   * <p>Note that if the expected type is text then this method does not throw, i.e. it checks for
   * compatible value types.
   * 
   * @param expected the expected count
   * @param observed the observed count
   * @throws IncorrectValueTypeException If the expected and observed do not match
   */
  public static void compatibleValueType(ValueType expected, ValueType observed) {
    // Text is compatible with any other type
    if (expected != ValueType.TEXT && expected != observed) {
      throw new IncorrectValueTypeException(expected, observed);
    }
  }

  /**
   * Find the next position of the index character in the value, starting from the given index.
   *
   * @param value the value
   * @param fromIndex the from index (should be positive)
   * @return the position (or {@link #NO_POSITION} if not found)
   */
  static int nextIndexCharacter(char[] value, int fromIndex) {
    final int max = value.length;
    for (int i = fromIndex; i < max; i++) {
      if (value[i] == INDEX_CHARACTER) {
        return i;
      }
    }
    return NO_POSITION;
  }

  /**
   * Gets the chars from the {@link StringBuilder}.
   *
   * @param sb the string builder
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
   * @param sequence the sequence
   * @return the chars
   */
  public static char[] getChars(CharSequence sequence) {
    // This will pass through Strings but if a StringBuilder
    // the dedicated method above should be called.
    return sequence.toString().toCharArray();
  }
}
