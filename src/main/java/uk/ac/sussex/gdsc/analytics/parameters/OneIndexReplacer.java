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
 * Class to replace a single occurrence of the index marker character with an index value.
 */
public class OneIndexReplacer {

  /** The expected number of indexes */
  private static final int EXPECTED = 1;

  /** The format. */
  private final char[] format;

  /** Length before the first index character in the format. */
  private final int length0;

  /** The position after first index character in the format. */
  private final int position1;

  /** Length after the first index character in the format. */
  private final int length1;

  /**
   * Create a new instance.
   *
   * @param nameFormat the name format
   * @throws IncorrectCountException If the index count is not one
   */
  public OneIndexReplacer(CharSequence nameFormat) {
    this(ParameterUtils.getChars(nameFormat));
  }

  /**
   * Create a new instance.
   *
   * @param parameter the parameter
   * @throws IncorrectCountException If the index count is not one
   */
  public OneIndexReplacer(ProtocolSpecification parameter) {
    this(parameter.getNameFormatRef());
  }

  /**
   * Create a new instance.
   *
   * @param parameter the parameter
   * @throws IncorrectCountException If the index count is not one
   */
  private OneIndexReplacer(char[] format) {
    this.format = format;
    // Find the positions
    int current = 0;
    int next;
    int count = 0;
    next = ParameterUtils.nextUnderscore(format, current);
    if (next == ParameterUtils.NO_POSITION) {
      throw new IncorrectCountException(EXPECTED, count);
    }
    // Count 1
    count++;
    length0 = next - current;
    current = next + 1;
    position1 = current;

    next = ParameterUtils.nextUnderscore(format, current);
    if (next != ParameterUtils.NO_POSITION) {
      throw new IncorrectCountException(EXPECTED, count + 1);
    }
    length1 = format.length - current;
  }

  /**
   * Gets the format.
   *
   * @return the format
   */
  public String getFormat() {
    return new String(format);
  }

  /**
   * Replace the index marker character in the format string with the given index and write the
   * result to the {@link StringBuilder}.
   *
   * <p>E.g. replace {@code cd_} with {@code cd2} for index 2.
   *
   * @param sb the string builder
   * @param index the index
   * @return the string builder
   */
  public StringBuilder replaceTo(StringBuilder sb, int index) {
    // No formats should have the index at the start, i.e. length0 == 0.
    //@formatter:off
    sb.append(format, 0, length0)
      .append(index);
    // Some formats do not have any character after the last index
    if (length1 != 0) {
      sb.append(format, position1, length1);
    }
    return sb;
  }
}
