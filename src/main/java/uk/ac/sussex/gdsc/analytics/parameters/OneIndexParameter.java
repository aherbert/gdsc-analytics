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
 * Base class to implements the {@link FormattedParameter} interface for a
 * {@link ParameterSpecification} with 1 index.
 */
abstract class OneIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 1;

  /** The index. */
  private final int index;

  /**
   * The index replacer.
   *
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private OneIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index the index
   * @throws IncorrectCountException If the index count is not one
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public OneIndexParameter(ParameterSpecification specification, int index) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index = ParameterUtils.requireStrictlyPositive(index, "Index");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index the index
   * @throws IncorrectCountException If the index count is not one
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public OneIndexParameter(ProtocolSpecification specification, int index) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
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
    OneIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      if (protocolSpecification == null) {
        replacer = new OneIndexReplacer(getParameterSpecification().getNameFormat());
      } else {
        // Use a cache of the defined protocol formats
        replacer =
            (OneIndexReplacer) IndexReplacerFactory.createIndexReplacer(protocolSpecification);
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index).append(Constants.EQUAL);
  }
}
