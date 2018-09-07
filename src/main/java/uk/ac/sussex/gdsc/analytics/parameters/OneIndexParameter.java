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

import java.util.EnumMap;

/**
 * Base class to implements the {@link FormattedParameter} interface for a
 * {@link ParameterSpecification} with 1 index.
 */
abstract class OneIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 1;

  /** Cache replacers for all the parameters. */
  private static final EnumMap<ProtocolSpecification, OneIndexReplacer> map =
      new EnumMap<>(ProtocolSpecification.class);

  /** The index. */
  private final int index;

  /**
   * The index replacer.
   * 
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private OneIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index the index
   * @throws IncorrectCountException If the index count is not one
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public OneIndexParameter(ParameterSpecification specification, int index) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index = ParameterUtils.requireStrictlyPositive(index, "Index");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index the index
   * @throws IncorrectCountException If the index count is not one
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public OneIndexParameter(ProtocolSpecification specification, int index) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index = ParameterUtils.requireStrictlyPositive(index, "Index");
  }

  /**
   * Gets the index.
   *
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    OneIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      // Use a cache of the defined protocol formats
      if (protocolSpecification != null) {
        replacer = map.computeIfAbsent(protocolSpecification, OneIndexParameter::newReplacer);
      } else {
        replacer = new OneIndexReplacer(getParameterSpecification().getNameFormat());
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index).append(Constants.EQUAL);
  }

  /**
   * Create a new replacer.
   *
   * @param parameter the parameter
   * @return the one index replacer
   */
  private static OneIndexReplacer newReplacer(ProtocolSpecification parameter) {
    return new OneIndexReplacer(parameter);
  }
}
