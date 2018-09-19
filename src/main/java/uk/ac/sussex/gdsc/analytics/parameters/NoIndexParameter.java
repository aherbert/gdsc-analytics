/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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
 * Base class to implements the {@link FormattedParameter} interface for a
 * {@link ParameterSpecification} with no indexes.
 */
abstract class NoIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 0;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not zero
   */
  public NoIndexParameter(ParameterSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @throws IncorrectCountException If the index count is not zero
   */
  public NoIndexParameter(ProtocolSpecification specification) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    if (protocolSpecification == null) {
      sb.append(getParameterSpecification().getNameFormat());
    } else {
      // Direct access to the char array
      final NoIndexReplacer replacer =
          (NoIndexReplacer) IndexReplacerFactory.createIndexReplacer(protocolSpecification);
      replacer.replaceTo(sb);
    }
    return sb.append(Constants.EQUAL);
  }
}
