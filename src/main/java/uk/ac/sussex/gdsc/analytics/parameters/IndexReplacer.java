/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2020 Alex Herbert
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
    sb.append(format, 0, ranges[0]);
    for (int i = 0; i < numberOfIndexes; i++) {
      sb.append(indexes[i]).append(format, ranges[2 * i + 1], ranges[2 * i + 2]);
    }
    return sb;
  }
}
