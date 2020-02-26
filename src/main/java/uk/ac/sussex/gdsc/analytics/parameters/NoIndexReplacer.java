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
 * Class to perform no replacement of the index marker character within the name format.
 *
 * <p>This is provided to allow all the name formats within the {@link ProtocolSpecification} to be
 * handled by specialisations of the {@link IndexReplacer}, i.e. those with 0, 1, 2, or 3 indexes.
 */
public class NoIndexReplacer extends IndexReplacer {

  /** The expected number of indexes. */
  private static final int EXPECTED = 0;

  /**
   * Create a new instance.
   *
   * @param nameFormat the name format
   * @throws IncorrectCountException If the index count is not zerp
   */
  public NoIndexReplacer(CharSequence nameFormat) {
    super(nameFormat);
    ParameterUtils.validateCount(EXPECTED, getNumberOfIndexes());
  }

  /**
   * Create a new instance.
   *
   * <p>Package scope. The public version is to use a factory method.
   *
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not zero
   */
  NoIndexReplacer(ProtocolSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  /**
   * Do not replace the index marker character in the format string. Write the format string
   * directly to the {@link StringBuilder}.
   *
   * @param sb the string builder
   * @return the string builder
   */
  public StringBuilder replaceTo(StringBuilder sb) {
    return sb.append(format);
  }
}
