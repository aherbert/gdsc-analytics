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
  public static String requireNotEmpty(String string, String message)
      throws IllegalArgumentException {
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
  public static int requirePositive(int value, String message) throws IllegalArgumentException {
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
  public static long requirePositive(long value, String message) throws IllegalArgumentException {
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
  public static int requireStrictlyPositive(int value, String message)
      throws IllegalArgumentException {
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
   * @see <a
   *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#tid">Tracking
   *      Id</a>
   */
  public static String validateTrackingId(String trackingId) throws IllegalArgumentException {
    if (!Pattern.matches("[A-Z]{2}-[0-9]{4,}-[0-9]", trackingId)) {
      throw new IllegalArgumentException("Invalid tracking id: " + trackingId);
    }
    return trackingId;
  }
}
