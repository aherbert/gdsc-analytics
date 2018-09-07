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
 * License adouble with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package uk.ac.sussex.gdsc.analytics.parameters;

/**
 * A generic number parameter for a {@link ParameterSpecification} with zero indexes.
 * 
 * <p>Stores the number value as a {@code double}.
 */
public class NoIndexNumberParameter extends NoIndexParameter {

  /** The required value type. */
  private static final ValueType REQUIRED = ValueType.NUMBER;

  /** The value. */
  private final double value;

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public NoIndexNumberParameter(ParameterSpecification specification, double value) {
    super(specification);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = value;
  }

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public NoIndexNumberParameter(ProtocolSpecification specification, double value) {
    super(specification);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = value;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public final double getValue() {
    return value;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    appendNameEquals(sb);
    ParameterUtils.appendNumberTo(sb, value);
    return sb;
  }
}
