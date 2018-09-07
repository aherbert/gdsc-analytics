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
 * Base class to implements the {@link FormattedParameter} interface for a {@link Parameter} with 1
 * index.
 */
abstract class OneIndexParameter extends BaseParameter {

  /** Cache replacers for all the parameters. */
  private static final EnumMap<Parameter, OneIndexReplacer> map = new EnumMap<>(Parameter.class);

  /** The index. */
  private final int index;

  /**
   * Create a new instance.
   *
   * @param parameter the parameter
   * @param index the index
   * @throws IncorrectCountException If the index count is not one
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public OneIndexParameter(Parameter parameter, int index) {
    super(parameter);
    ParameterUtils.validateCount(parameter.getNumberOfIndexes(), 1);
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
    final OneIndexReplacer indexReplacer =
        map.computeIfAbsent(getParameter(), OneIndexParameter::newReplacer);
    return indexReplacer.replaceTo(sb, index).append(ParameterUtils.EQUAL);
  }

  /**
   * Create a new replacer.
   *
   * @param parameter the parameter
   * @return the one index replacer
   */
  private static OneIndexReplacer newReplacer(Parameter parameter) {
    return new OneIndexReplacer(parameter);
  }
}
