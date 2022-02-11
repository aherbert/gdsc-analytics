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

import java.util.EnumSet;

/**
 * Defines a parameter specification for the Google Analytics Measurement Protocol.
 *
 * <p>Parameters are expected to be {@code name=value} pairs.
 *
 * @see <a href= "http://goo.gl/a8d4RP">Measurement Protocol Parameter Reference</a>
 */
public interface ParameterSpecification {

  /**
   * Gets the formal name of the parameter, e.g. Tracking ID.
   *
   * @return the formal name
   */
  String getFormalName();

  /**
   * Gets the format for the {@code name} part of the parameter {@code name=value} pair.
   *
   * <p>This can contain zero of more indexes. Indexes will be identified with the {@code _}
   * character, for example {@code pr_cc} for Product Coupon Code where the {@code _} represents the
   * product index.
   *
   * <p>WARNING: It is assumed the format will not required URL encoding. The format should contain
   * only URL safe ASCII characters as defined in UFC 2396.
   *
   * @return the name format
   * @see <a href= "http://goo.gl/a8d4RP#pr_cc">Product Coupon Code</a>
   * @see #getNumberOfIndexes()
   * @see Constants#UNDERSCORE
   * @see <a href="https://www.ietf.org/rfc/rfc2396.txt">RFC 2396: Uniform Resource Identifiers
   *      (URI): Generic Syntax</a>
   */
  CharSequence getNameFormat();

  /**
   * Gets the number of indexes.
   *
   * <p>This is a count of the number of {@code _} (underscore) characters within the name format.
   *
   * @return the number of indexes
   * @see ParameterSpecification#getNameFormat()
   * @see Constants#UNDERSCORE
   */
  default int getNumberOfIndexes() {
    return ParameterUtils.countIndexes(getNameFormat());
  }

  /**
   * Gets the type used for the parameter value.
   *
   * @return the value type
   */
  ValueType getValueType();

  /**
   * Gets the max length for the parameter value.
   *
   * <p>It is only relevant for {@link ValueType#TEXT} parameters.
   *
   * <p>If zero then any length is allowed.
   *
   * <p>Otherwise this specifies the maximum bytes for the {@code value} part of the parameter
   * {@code name=value} pair.
   *
   * <p>The default is {@code 0}.
   *
   * @return the max length
   */
  default int getMaxLength() {
    return 0;
  }

  /**
   * Gets the supported hit types for the parameter.
   *
   * <p>If the set is {@code null} then it can be assumed that all hit types are supported since
   * supporting no hit types is not valid.
   *
   * <p>For convenience the default is a set of all hit types.
   *
   * @return the supported hit types
   * @see #isSupported(HitType)
   */
  default EnumSet<HitType> getSupportedHitTypes() {
    return Constants.ALL_OF_HIT_TYPE;
  }

  /**
   * Checks the hit type is supported.
   *
   * <p>If the argument is {@code null} then this will return {@code false}.
   *
   * <p>The default method checks the hit type is present in the set returned from
   * {@link #getSupportedHitTypes()}. If the set is {@code null} then it is assumed
   * that all hit types are supported since supporting no hit types is not valid.
   *
   * @param hitType the hit type
   * @return true, if is supported
   */
  default boolean isSupported(HitType hitType) {
    if (hitType == null) {
      return false;
    }
    final EnumSet<HitType> supportedHitTypes = getSupportedHitTypes();
    return supportedHitTypes == null || supportedHitTypes.contains(hitType);
  }
}
