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
 * Appends formatted parameter or parameters to a URL.
 *
 * <p>The parameters are expected to add one or more {@code name=value} pairs. Strings should be
 * appropriately URL encoded.
 *
 * <p>If multiple pairs are added then the '<strong>&amp;</strong>' character is expected to
 * separate each pair, e.g. {@code name=value&name2=value2}.
 *
 * <p>This is a functional interface whose functional method is {@link #appendTo(StringBuilder)}.
 */
@FunctionalInterface
public interface FormattedParameter {

  /**
   * Format the parameter(s) to the provided {@link StringBuilder}.
   *
   * <p>The parameters are expected to add one or more {@code name=value} pairs. {@code value}
   * Strings should be appropriately URL encoded.
   *
   * <p>If multiple pairs are added then the '<strong>&amp;</strong>' character is expected to
   * separate each pair, e.g. {@code name=value&name2=value2}.
   *
   * <p>Note: The '<strong>&amp;</strong>' character <strong>must not</strong> be included at the
   * start. It should be assumed the {@link StringBuilder} is empty.
   *
   * <p>Parameters can be added to an existing URL using the function
   * {@link #appendTo(StringBuilder)}.
   *
   * @param sb the string builder
   * @return the string builder
   * @see #appendTo(StringBuilder)
   */
  StringBuilder formatTo(StringBuilder sb);

  /**
   * Append parameter(s) to the existing URL within the provided {@link StringBuilder}.
   *
   * <p>If the {@link StringBuilder} is empty this calls {@link #formatTo(StringBuilder)}.
   *
   * <p>If the {@link StringBuilder} is not empty this adds the '<strong>&amp;</strong>' character
   * and calls {@link #formatTo(StringBuilder)}.
   *
   * <p>This method allows consecutive {@link FormattedParameter} instances to be formatted to the
   * same {@link StringBuilder} using repeats calls to {@link #appendTo(StringBuilder)}.
   *
   * @param sb the string builder
   * @return the string builder
   */
  default StringBuilder appendTo(StringBuilder sb) {
    if (sb.length() != 0) {
      sb.append(Constants.AND);
    }
    return formatTo(sb);
  }

  /**
   * Return the formatted string.
   *
   * <p>The default implementation creates a new {@link StringBuilder} and calls
   * {@link #formatTo(StringBuilder)} to obtain the string.
   *
   * @return the string
   */
  default String format() {
    return formatTo(new StringBuilder()).toString();
  }

  /**
   * Creates a fixed version of the {@link FormattedParameter}.
   *
   * <p>The default implementation creates a formatted string of the parameter(s) using
   * {@link #formatTo(StringBuilder)} and returns a {@link FormattedParameter} containing the
   * formatted string.
   *
   * <p>This is a utility method that can be used to freeze a complex collection of parameters to a
   * fixed version.
   *
   * @return the string
   */
  default FormattedParameter freeze() {
    // Note: This may be empty if the interface is abused,
    // i.e. a formatTo(...) functional method is provided that
    // does nothing.
    final StringBuilder sb = formatTo(new StringBuilder());
    if (sb.length() == 0) {
      return empty();
    }
    // Create a lightweight copy of the characters.
    // This is done for better support of the appendTo2(sb) method.
    // - append(char[]) method in AbtsractStringBuilder just calls System.arraycopy
    // - append(String) method in AbtsractStringBuilder checks for null and
    // delegates work to the String class which does extra bounds checks.
    // Note: sb.toString() would create a copy of the internal char[] array
    // for a new String.
    // Here we do the same.
    final char[] chars = new char[sb.length()];
    sb.getChars(0, sb.length(), chars, 0);

    // Partially implement the interface
    return new FormattedParameter() {
      @Override
      public StringBuilder formatTo(StringBuilder sb) {
        return sb.append(chars);
      }

      @Override
      public FormattedParameter freeze() {
        // Already frozen
        return this;
      }
    };
  }

  /**
   * Create an empty {@link FormattedParameter}.
   *
   * <p>The returned implementation will behave as if the parameters where the empty String
   * {@code ""}.
   *
   * @return the empty parameters
   */
  static FormattedParameter empty() {
    // Fully implement the interface to do nothing
    return new FormattedParameter() {
      @Override
      public StringBuilder formatTo(StringBuilder sb) {
        return sb;
      }

      @Override
      public StringBuilder appendTo(StringBuilder sb) {
        return sb;
      }

      @Override
      public FormattedParameter freeze() {
        // Already simplified
        return this;
      }
    };
  }
}
