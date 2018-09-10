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
