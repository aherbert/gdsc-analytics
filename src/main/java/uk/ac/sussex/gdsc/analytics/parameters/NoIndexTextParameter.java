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

import java.util.Objects;

/**
 * A generic text parameter for a {@link ParameterSpecification} with zero indexes.
 */
public class NoIndexTextParameter extends NoIndexParameter {

  /** The required value type. */
  private static final ValueType REQUIRED = ValueType.TEXT;

  /** The value. */
  private final String value;

  /** The encoded value. */
  private String encodedValue;

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public NoIndexTextParameter(ParameterSpecification specification, String value) {
    super(specification);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = Objects.requireNonNull(value, "Value");
  }

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public NoIndexTextParameter(ProtocolSpecification specification, String value) {
    super(specification);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = Objects.requireNonNull(value, "Value");
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public final String getValue() {
    return value;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    String encoded = encodedValue;
    if (encoded == null) {
      encoded = UrlEncoderHelper.encode(value);
      encodedValue = encoded;
    }
    return appendNameEquals(sb).append(encoded);
  }
}
