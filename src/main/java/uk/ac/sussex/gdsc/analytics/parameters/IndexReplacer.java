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
 * Class to replace occurrences of the index marker character with index values.
 * 
 * @see Constants#UNDERSCORE
 */
public class IndexReplacer {

  /** The number of indexes. */
  private final int numberOfIndexes;

  /** The format. */
  protected final char[] format;

  /**
   * Contains alternating lengths and positions in the format array.
   * 
   * <p>The first item is the length before the first index character in the format.
   * 
   * <p>The second item is the position after the first index character.
   * 
   * <p>On so on until the final item is the length after the last index character.
   * 
   * <p>The array will be size {@code numberOfIndexes * 2 + 1}.
   */
  protected final int[] ranges;

  /**
   * Create a new instance.
   *
   * @param nameFormat the name format
   */
  public IndexReplacer(CharSequence nameFormat) {
    this.format = ParameterUtils.getChars(nameFormat);
    this.numberOfIndexes = ParameterUtils.countIndexes(nameFormat);
    ranges = new int[numberOfIndexes * 2 + 1];
    initialise();
  }

  /**
   * Create a new instance.
   * 
   * <p>Package scope. The public version is to use a factory method forcing the compiled
   * replacement pattern to be cached.
   *
   * @param specification the specification
   */
  IndexReplacer(ProtocolSpecification specification) {
    this.format = specification.getNameFormat().toCharArray();
    this.numberOfIndexes = specification.getNumberOfIndexes();
    ranges = new int[numberOfIndexes * 2 + 1];
    initialise();
  }

  /**
   * Initialise.
   */
  private void initialise() {
    int count = 0;
    int current = 0;
    int pos = ParameterUtils.nextUnderscore(format, current);
    while (pos != ParameterUtils.NO_POSITION) {
      // Length before index
      ranges[count++] = pos - current;
      // Position after index
      current = pos + 1;
      ranges[count++] = current;
      pos = ParameterUtils.nextUnderscore(format, current);
    }
    // Final length
    ranges[count] = format.length - current;
  }

  /**
   * Gets the number of indexes.
   *
   * @return the number of indexes
   */
  public int getNumberOfIndexes() {
    return numberOfIndexes;
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
   * <p>Must be called with the correct number of index arguments.
   *
   * @param sb the string builder
   * @param indexes the indexes
   * @return the string builder
   * @throws IncorrectCountException If the number of indexes is incorrect
   * @see #getNumberOfIndexes()
   */
  public StringBuilder replaceTo(StringBuilder sb, int... indexes) {
    if (numberOfIndexes != indexes.length) {
      throw new IncorrectCountException(numberOfIndexes, indexes.length);
    }
    // No formats should have the index at the start, i.e. length0 == 0.
    sb.append(format, 0, ranges[0]);
    for (int i = 0, j = 1; i < numberOfIndexes; i++, j += 2) {
      sb.append(indexes[i]).append(format, ranges[j], ranges[j + 1]);
    }
    return sb;
  }
}
