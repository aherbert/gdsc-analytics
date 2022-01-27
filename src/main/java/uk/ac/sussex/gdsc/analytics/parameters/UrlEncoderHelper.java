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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * URL encoder to encode strings using UTF-8.
 *
 * <p>Checks for any characters that require encoding and handles the simple case of changing ' '
 * (space) to '+'. Otherwise tÏhe actual encoding is performed by
 * {@link URLEncoder#encode(String, String)}.
 *
 * @see <a href="https://www.ietf.org/rfc/rfc2396.txt">RFC 2396: Uniform Resource Identifiers (URI):
 *      Generic Syntax</a>
 */
public final class UrlEncoderHelper {

  /** The characters that do not require encoding. */
  private static final boolean[] NO_ENCODING;
  /** The space character. This is replaced with '+'. */
  private static final char SPACE = ' ';
  /** The plus '+' character. */
  private static final char PLUS = '+';
  /** The size required for the lower 7-bits of the ASCII table. */
  private static final int ASCII_SIZE = 128;
  /**
   * The result for no index in {@link String#indexOf(int)}.
   */
  private static final int NO_INDEX = -1;

  static {

    // Note:
    // Encoding is done using java.net.URLEncoder.
    // Build a look-up table to scan a String to check for any characters that require encoding.
    // If none are present then no encoding is required. An exception is made for the space
    // character that can be simply encoded using a '+' character.

    // From the URLEncoder javadoc:
    // - The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain the
    // same.
    // - The special characters ".", "-", "*", and "_" remain the same.
    // - The space character " " is converted into a plus sign "+".
    // - All other characters are unsafe and are first converted into one or more bytes using some
    // encoding scheme. Then each byte is represented by the 3-character string "%xy", where xy is
    // the two-digit hexadecimal representation of the byte. The recommended encoding scheme to use
    // is UTF-8. However, for compatibility reasons, if an encoding is not specified, then the
    // default encoding of the platform is used.

    NO_ENCODING = new boolean[ASCII_SIZE];
    for (int i = 'a'; i <= 'z'; i++) { // ASCII 97 - 122
      NO_ENCODING[i] = true;
    }
    for (int i = 'A'; i <= 'Z'; i++) { // ASCII 65 - 90
      NO_ENCODING[i] = true;
    }
    for (int i = '0'; i <= '9'; i++) { // ASCII 48 - 57
      NO_ENCODING[i] = true;
    }
    NO_ENCODING[' '] = true; // ASCII 32
    NO_ENCODING['-'] = true; // ASCII 45
    NO_ENCODING['_'] = true; // ASCII 95
    NO_ENCODING['.'] = true; // ASCII 46
    NO_ENCODING['*'] = true; // ASCII 42
  }

  /**
   * No public instances.
   */
  private UrlEncoderHelper() {
    // Do nothing
  }

  /**
   * Encode the string using UTF-8.
   *
   * <p>A check is made for any characters that require encoding. If {@code false} the same string
   * is returned.
   *
   * <p>Otherwise the actual encoding is performed by {@link URLEncoder#encode(String, String)}.
   *
   * @param string The string
   * @return The encoded string
   */
  public static String encode(String string) {
    return (noEncodingRequired(string))
        // Handle special case of space character
        ? spaceEncode(string)
        // Delagate encoding
        : urlEncode(string, "UTF-8");
  }

  /**
   * Encode the string substituting the space character for '+'.
   *
   * @param string the string
   * @return the string
   */
  private static String spaceEncode(String string) {
    if (string.indexOf(SPACE) == NO_INDEX) {
      // No substitution required
      return string;
    }
    // Switch space to '+'
    final char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == SPACE) {
        chars[i] = PLUS;
      }
    }
    return new String(chars);
  }

  /**
   * Encode the string using the given encoding.
   *
   * @param string The string
   * @param encoding The encoding (UTF-8 is recommended)
   * @return The encoded string
   * @throws UnsupportedEncodingRuntimeException If the encoding is not supported
   */
  static String urlEncode(String string, String encoding) {
    try {
      return URLEncoder.encode(string, encoding);
    } catch (final UnsupportedEncodingException ex) {
      // UTF-8 is required by the Java platform so this should not happen
      Logger.getLogger(UrlEncoderHelper.class.getName())
          .severe("Unsupported encoding: " + ex.getMessage());
      throw new UnsupportedEncodingRuntimeException(ex);
    }
  }

  /**
   * Check if the string contains only characters that will not be encoded.
   *
   * @param string the string
   * @return true, if no encoded characters are present
   */
  public static boolean noEncodingRequired(String string) {
    Objects.requireNonNull(string, "The string is null");
    for (int i = 0; i < string.length(); i++) {
      if (!noEncodingRequired(string.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the character will not be encoded.
   *
   * @param ch the character
   * @return true, if no encoding is required
   */
  public static boolean noEncodingRequired(char ch) {
    final int i = ch;
    // Char is unsigned so no need to check below zero.
    return i < NO_ENCODING.length && NO_ENCODING[i];
  }
}
