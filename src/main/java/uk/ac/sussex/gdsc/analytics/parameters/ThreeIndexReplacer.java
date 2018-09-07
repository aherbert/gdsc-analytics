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
 * Class to replace three occurrences of the index marker character with index values.
 */
public class ThreeIndexReplacer {

  /** The expected number of indexes. */
  private static final int EXPECTED = 3;

  /** The format. */
  private final char[] format;

  /** Length before the first index character in the format. */
  private final int length0;

  /** The position after first index character in the format. */
  private final int position1;

  /** Length after the first index character in the format. */
  private final int length1;

  /** The position after the second index character in the format. */
  private final int position2;

  /** Length after the second index character in the format. */
  private final int length2;

  /** The position after third index character in the format. */
  private final int position3;

  /** The length after the third index character in the format. */
  private final int length3;

  /**
   * Create a new instance.
   *
   * @param nameFormat the name format
   * @throws IncorrectCountException If the index count is not three
   */
  public ThreeIndexReplacer(CharSequence nameFormat) {
    this(ParameterUtils.getChars(nameFormat));
  }

  /**
   * Create a new instance.
   *
   * @param parameter the parameter
   * @throws IncorrectCountException If the index count is not three
   */
  public ThreeIndexReplacer(ProtocolSpecification parameter) {
    this(parameter.getNameFormatRef());
  }

  /**
   * Create a new instance.
   *
   * @param format the format
   * @throws IncorrectCountException If the index count is not three
   */
  private ThreeIndexReplacer(char[] format) {
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
    if (next == ParameterUtils.NO_POSITION) {
      throw new IncorrectCountException(EXPECTED, count);
    }
    // Count 2
    count++;
    length1 = next - current;
    current = next + 1;
    position2 = current;

    next = ParameterUtils.nextUnderscore(format, current);
    if (next == ParameterUtils.NO_POSITION) {
      throw new IncorrectCountException(EXPECTED, count);
    }
    // Count 3
    count++;
    length2 = next - current;
    current = next + 1;
    position3 = current;

    next = ParameterUtils.nextUnderscore(format, current);
    if (next != ParameterUtils.NO_POSITION) {
      throw new IncorrectCountException(EXPECTED, count + 1);
    }
    length3 = format.length - current;
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
   * Replace the index marker character in the format string with the given indexes and write the
   * result to the {@link StringBuilder}.
   * 
   * <p>E.g. replace {@code il_pi_cd_} with {@code il2pi5cd4} for indexes 2, 5 and 4.
   *
   * @param sb the string builder
   * @param index1 the first index
   * @param index2 the second index
   * @param index3 the third index
   * @return the string builder
   */
  public StringBuilder replaceTo(StringBuilder sb, int index1, int index2, int index3) {
    // No formats should have the index at the start, i.e. length0 == 0.
    //@formatter:off
    sb.append(format, 0, length0)
      .append(index1).append(format, position1, length1)
      .append(index2).append(format, position2, length2)
      .append(index3);
    // Some formats do not have any character after the last index
    if (length3 != 0) {
      sb.append(format, position3, length3);
    }
    return sb;
  }
}
