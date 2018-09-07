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

/**
 * Helper class for appending encoded data to a URL parameter string.
 */
public final class FormattedParameterHelper {

  /**
   * Private constructor.
   */
  private FormattedParameterHelper() {}

  /**
   * Append {@code key=value}.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, String value) {
    sb.append(key).append('=').append(UrlEncoderHelper.encode(value));
  }

  /**
   * Append {@code key=value}.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, int value) {
    sb.append(key).append('=').append(value);
  }

  /**
   * Append {@code key=value}.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, double value) {
    sb.append(key).append('=').append(value);
  }

  /**
   * Append {@code key=value}.
   *
   * @param sb the string builder
   * @param key the key
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, boolean value) {
    sb.append(key).append('=').append((value) ? '1' : '0');
  }

  /**
   * Append {@code key+keyId=value} where {@code key+keyId} is the equivalent of the string result
   * of adding the key and the key Id.
   *
   * @param sb the string builder
   * @param key the key
   * @param keyId the key id
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, int keyId, String value) {
    sb.append(key).append(keyId).append('=').append(UrlEncoderHelper.encode(value));
  }

  /**
   * Append {@code key+keyId=value} where {@code key+keyId} is the equivalent of the string result
   * of adding the key and the key Id.
   *
   * @param sb the string builder
   * @param key the key
   * @param keyId the key id
   * @param value the value
   */
  public static void append(StringBuilder sb, String key, int keyId, int value) {
    sb.append(key).append(keyId).append('=').append(value);
  }
}
