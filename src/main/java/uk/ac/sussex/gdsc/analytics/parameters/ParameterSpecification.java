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
   * @return the max length
   */
  default int getMaxLength() {
    return 0;
  }

  /**
   * Gets the supported hit types for the parameter.
   *
   * <p>If null then all types are supported. Otherwise the supported types are returned.
   * 
   * @return the supported hit types
   * @see #isSupported(HitType)
   */
  default HitType[] getSupportedHitTypes() {
    return null;
  }

  /**
   * Checks the hit type is supported.
   * 
   * <p>If the argument is {@code null} then this will return {@code false}. The exception to this
   * is if the {@code null} hit type is present in the array returned from
   * {@link #getSupportedHitTypes()}.
   * 
   * @param hitType the hit type
   * @return true, if is supported
   */
  default boolean isSupported(HitType hitType) {
    final HitType[] supportedHitTypes = getSupportedHitTypes();
    if (supportedHitTypes == null) {
      // Don't support the null hit type!
      return hitType != null;
    }
    // This assumes that supported hit types will never contain a null
    for (HitType supported : supportedHitTypes) {
      if (supported == hitType) {
        return true;
      }
    }
    return false;
  }
}
