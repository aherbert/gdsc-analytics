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
 * {@link ParameterSpecification} with 3 indexes.
 */
abstract class ThreeIndexParameter extends BaseParameter {

  /** The expected number of indexes */
  private static final int EXPECTED = 3;

  /** Cache replacers for all the parameters. */
  private static final EnumMap<ProtocolSpecification, ThreeIndexReplacer> map =
      new EnumMap<>(ProtocolSpecification.class);

  /** The first index. */
  private final int index1;
  /** The second index. */
  private final int index2;
  /** The third index. */
  private final int index3;

  /**
   * The index replacer.
   * 
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private ThreeIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param index3 the third index
   * @throws IncorrectCountException If the index count is not three
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public ThreeIndexParameter(ParameterSpecification specification, int index1, int index2,
      int index3) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
    this.index3 = ParameterUtils.requireStrictlyPositive(index3, "Index3");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param index3 the third index
   * @throws IncorrectCountException If the index count is not three
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public ThreeIndexParameter(ProtocolSpecification specification, int index1, int index2,
      int index3) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
    this.index3 = ParameterUtils.requireStrictlyPositive(index3, "Index3");
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

  /**
   * Gets the third index.
   *
   * @return the third index
   */
  public int getIndex3() {
    return index3;
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    ThreeIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      // Use a cache of the defined protocol formats
      if (protocolSpecification != null) {
        replacer = map.computeIfAbsent(protocolSpecification, ThreeIndexParameter::newReplacer);
      } else {
        replacer = new ThreeIndexReplacer(getParameterSpecification().getNameFormat());
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index1, index2, index3).append(Constants.EQUAL);
  }

  /**
   * Create a new replacer.
   *
   * @param parameter the parameter
   * @return the three index replacer
   */
  private static ThreeIndexReplacer newReplacer(ProtocolSpecification parameter) {
    return new ThreeIndexReplacer(parameter);
  }
}
