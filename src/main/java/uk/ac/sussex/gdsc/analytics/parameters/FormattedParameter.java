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
 * Appends formatted parameter or parameters to a URL.
 * 
 * <p>This is a functional interface whose functional method is {@link #appendTo(StringBuilder)}.
 */
@FunctionalInterface
public interface FormattedParameter {

  /**
   * Append parameter(s) to the provided {@link StringBuilder}.
   * 
   * <p>The parameters are expected to add one or more {@code &name=value} pairs. {@code value}
   * Strings should be appropriately URL encoded.
   * 
   * <p>Note: The '<strong>&amp;</strong>' character <strong>must</strong> be included.
   *
   * <p>A correct parameter URL with no '<strong>&amp;</strong>' character can be obtained from the
   * function {@link #toPostString()}.
   * 
   * <p>This method allows consecutive {@link FormattedParameter} instances can be formatted to the
   * same {@link StringBuilder} using repeats calls to {@link #appendTo(StringBuilder)}.
   * 
   * @param sb the string builder
   * @see #toGetString()
   */
  void appendTo(StringBuilder sb);

  /**
   * Create a formatted string of parameter(s) for a HTTP POST URL.
   *
   * <p>This is a utility method that creates a new string builder, then calls
   * {@link #appendTo(StringBuilder)} and returns the result without the leading
   * '<strong>&amp;</strong>' character for HTTP POST.
   * 
   * @return the string
   */
  default String toPostString() {
    final StringBuilder sb = new StringBuilder();
    appendTo(sb);
    if (sb.length() != 0) {
      sb.deleteCharAt(0);
    }
    return sb.toString();
  }

  /**
   * Create a formatted string of parameter(s) for a HTTP GET URL.
   *
   * <p>This is a utility method that creates a new string builder, then calls
   * {@link #appendTo(StringBuilder)} and returns the result with the leading
   * '<strong>&amp;</strong>' character replace with '<strong>?</strong>' for HTTP GET.
   * 
   * @return the string
   */
  default String toGetString() {
    final StringBuilder sb = new StringBuilder();
    appendTo(sb);
    if (sb.length() != 0) {
      sb.setCharAt(0, '?');
    }
    return sb.toString();
  }

  /**
   * Create a formatted string of the parameter(s) using {@link #appendTo(StringBuilder)} and return
   * a {@link FormattedParameter} containing the preformatted string.
   *
   * <p>This is a utility method that can be used to simplify a complex collection of parameters.
   * 
   * @return the string
   */
  default FormattedParameter simplify() {
    // Note: This may be empty if the interface is abused,
    // i.e. a formatTo(...) functional method is provided that
    // does nothing.
    final StringBuilder sb = new StringBuilder();
    appendTo(sb);
    if (sb.length() == 0) {
      return empty();
    }
    // Create a lightweight copy of the characters
    final char[] chars = new char[sb.length()];
    sb.getChars(0, sb.length(), chars, 0);
    // Partially implement the interface
    return new FormattedParameter() {
      @Override
      public void appendTo(StringBuilder sb) {
        sb.append(chars);
      }

      @Override
      public FormattedParameter simplify() {
        // Already simplified
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
      public void appendTo(StringBuilder sb) {
        // Nothing to add
      }

      @Override
      public String toPostString() {
        // Empty string
        return "";
      }

      @Override
      public String toGetString() {
        // Empty string
        return "";
      }

      @Override
      public FormattedParameter simplify() {
        // Already simplified
        return this;
      }
    };
  }
}
