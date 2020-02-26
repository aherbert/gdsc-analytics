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
 * {@link ParameterSpecification} with 2 indexes.
 */
abstract class TwoIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 2;

  /** The first index. */
  private final int index1;
  /** The second index. */
  private final int index2;

  /**
   * The index replacer.
   *
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private TwoIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @throws IncorrectCountException If the index count is not two
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public TwoIndexParameter(ParameterSpecification specification, int index1, int index2) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @throws IncorrectCountException If the index count is not two
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public TwoIndexParameter(ProtocolSpecification specification, int index1, int index2) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
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

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    TwoIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      if (protocolSpecification == null) {
        replacer = new TwoIndexReplacer(getParameterSpecification().getNameFormat());
      } else {
        // Use a cache of the defined protocol formats
        replacer =
            (TwoIndexReplacer) IndexReplacerFactory.createIndexReplacer(protocolSpecification);
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index1, index2).append(Constants.EQUAL);
  }
}
