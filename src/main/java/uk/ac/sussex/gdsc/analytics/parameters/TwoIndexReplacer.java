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
 * Class to replace two occurrences of the index marker character with index values.
 */
public class TwoIndexReplacer extends IndexReplacer {

  /** The expected number of indexes. */
  private static final int EXPECTED = 2;

  /**
   * Create a new instance.
   *
   * @param nameFormat the name format
   * @throws IncorrectCountException If the index count is not two
   */
  public TwoIndexReplacer(CharSequence nameFormat) {
    super(nameFormat);
    ParameterUtils.validateCount(EXPECTED, getNumberOfIndexes());
  }

  /**
   * Create a new instance.
   *
   * <p>Package scope. The public version is to use a factory method.
   * 
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not two
   */
  TwoIndexReplacer(ProtocolSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  /**
   * Replace the index marker character in the format string with the given indexes and write the
   * result to the {@link StringBuilder}.
   * 
   * <p>E.g. replace {@code il_pi_} with {@code il2pi5} for indexes 2 and 5.
   *
   * @param sb the string builder
   * @param index1 the first index
   * @param index2 the second index
   * @return the string builder
   */
  public StringBuilder replaceTo(StringBuilder sb, int index1, int index2) {
    // No formats should have the index at the start, i.e. length0 == 0.
    //@formatter:off
    sb.append(format, 0, ranges[0])
      .append(index1).append(format, ranges[1], ranges[2])
      .append(index2);
    // Some formats do not have any character after the last index
    if (ranges[4] != 0) {
      sb.append(format, ranges[3], ranges[4]);
    }
    return sb;
  }
}
