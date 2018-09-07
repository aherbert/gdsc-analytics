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
 * {@link ParameterSpecification} with 2 indexes.
 */
abstract class TwoIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 2;

  /** Cache replacers for all the parameters. */
  private static final EnumMap<ProtocolSpecification, TwoIndexReplacer> map =
      new EnumMap<>(ProtocolSpecification.class);

  /** The first index. */
  private final int index1;
  /** The second index. */
  private final int index2;

  /**
   * The index replacer.
   * 
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private TwoIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @throws IncorrectCountException If the index count is not two
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public TwoIndexParameter(ParameterSpecification specification, int index1, int index2) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @throws IncorrectCountException If the index count is not two
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public TwoIndexParameter(ProtocolSpecification specification, int index1, int index2) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
  }

  /**
   * Gets the first index.
   *
   * @return the first index
   */
  public int getIndex1() {
    return index1;
  }

  /**
   * Gets the second index.
   *
   * @return the second index
   */
  public int getIndex2() {
    return index2;
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    TwoIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      // Use a cache of the defined protocol formats
      if (protocolSpecification != null) {
        replacer = map.computeIfAbsent(protocolSpecification, TwoIndexParameter::newReplacer);
      } else {
        replacer = new TwoIndexReplacer(getParameterSpecification().getNameFormat());
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index1, index2).append(Constants.EQUAL);
  }

  /**
   * Create a new replacer.
   *
   * @param parameter the parameter
   * @return the two index replacer
   */
  private static TwoIndexReplacer newReplacer(ProtocolSpecification parameter) {
    return new TwoIndexReplacer(parameter);
  }
}
