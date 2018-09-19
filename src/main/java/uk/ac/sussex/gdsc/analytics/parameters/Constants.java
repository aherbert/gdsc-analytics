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

/**
 * Contains constants.
 */
public final class Constants {

  /**
   * The '<strong>{@code =}</strong>' (Equal) character.
   *
   * <p>Used to to create a {@code name=value} pair for a URL.
   */
  public static final char EQUAL = '=';

  /**
   * The '<strong>{@code &}</strong>' (Ampersand) character.
   *
   * <p>Used to join {@code name=value} pairs for a URL, e.g. {@code name=value&name2=value2}.
   */
  public static final char AND = '&';

  /**
   * The '<strong>{@code _}</strong>' (Underscore) character.
   *
   * <p>The character used to identify an index within the name format for the {@code name=value}
   * parameter pair, e.g. {@code cm_} where {@code _} is the index of the custom metric {@code cm}
   * parameter.
   */
  public static final char UNDERSCORE = '_';

  /**
   * The '<strong>{@code /}</strong>' (forward slash) character.
   *
   * <p>The character required at the start of a document path for a URL.
   */
  public static final char FORWARD_SLASH = '/';

  /**
   * The empty string "".
   */
  public static final String EMPTY_STRING = "";

  /**
   * A zero length char array.
   */
  public static final char[] EMPTY_CHARS = new char[0];

  /**
   * No public construction.
   */
  private Constants() {
    // Do nothing
  }
}
