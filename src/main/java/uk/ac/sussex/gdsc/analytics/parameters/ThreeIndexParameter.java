/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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
 * {@link ParameterSpecification} with 3 indexes.
 */
abstract class ThreeIndexParameter extends BaseParameter {

  /** The expected number of indexes. */
  private static final int EXPECTED = 3;

  /** The first index. */
  private final int index1;
  /** The second index. */
  private final int index2;
  /** The third index. */
  private final int index3;

  /**
   * The index replacer.
   *
   * <p>This is from the the cache if the parameter is a {@link ProtocolSpecification}, otherwise it
   * is a custom generated replacer unique to this instance.
   */
  private ThreeIndexReplacer indexReplacer;

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param index3 the third index
   * @throws IncorrectCountException If the index count is not three
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public ThreeIndexParameter(ParameterSpecification specification, int index1, int index2,
      int index3) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
    this.index3 = ParameterUtils.requireStrictlyPositive(index3, "Index3");
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param index3 the third index
   * @throws IncorrectCountException If the index count is not three
   * @throws IllegalArgumentException If the index is strictly positive
   */
  public ThreeIndexParameter(ProtocolSpecification specification, int index1, int index2,
      int index3) {
    super(specification);
    ParameterUtils.validateCount(EXPECTED, specification);
    this.index1 = ParameterUtils.requireStrictlyPositive(index1, "Index1");
    this.index2 = ParameterUtils.requireStrictlyPositive(index2, "Index2");
    this.index3 = ParameterUtils.requireStrictlyPositive(index3, "Index3");
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

  /**
   * Gets the third index.
   *
   * @return the third index
   */
  public int getIndex3() {
    return index3;
  }

  @Override
  protected StringBuilder appendNameEquals(StringBuilder sb) {
    ThreeIndexReplacer replacer = this.indexReplacer;
    if (replacer == null) {
      if (protocolSpecification == null) {
        replacer = new ThreeIndexReplacer(getParameterSpecification().getNameFormat());
      } else {
        // Use a cache of the defined protocol formats
        replacer =
            (ThreeIndexReplacer) IndexReplacerFactory.createIndexReplacer(protocolSpecification);
      }
      indexReplacer = replacer;
    }
    return replacer.replaceTo(sb, index1, index2, index3).append(Constants.EQUAL);
  }
}
