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
 * Base class to implements the {@link FormattedParameter} interface for a {@link ParameterSpecification} with
 * no indexes.
 */
abstract class NoIndexParameter extends BaseParameter {

  /** The expected number of indexes */
  private static final int EXPECTED = 0;
  
  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not zero
   */
  public NoIndexParameter(ParameterSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not zero
   */
  public NoIndexParameter(ProtocolSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    if (protocolSpecification != null) {
      // Direct access to the char array
      sb.append(protocolSpecification.getNameFormatRef());
    } else {
      sb.append(getParameterSpecification().getNameFormat());
    }
    return sb.append(Constants.EQUAL);
  }
}
