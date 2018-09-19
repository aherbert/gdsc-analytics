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

import java.util.EnumMap;

/**
 * A factory for creating {@link IndexReplacer} objects.
 */
public final class IndexReplacerFactory {

  /** The constant 1. */
  private static final int ONE = 1;
  /** The constant 2. */
  private static final int TWO = 2;

  /** Cache replacers for all the parameters. */
  private static final EnumMap<ProtocolSpecification, IndexReplacer> cache =
      new EnumMap<>(ProtocolSpecification.class);

  /**
   * No public creation.
   */
  private IndexReplacerFactory() {
    // Do nothing
  }

  /**
   * Creates the index replacer.
   *
   * <p>This method is guaranteed to return a dedicated 0, 1, 2, or 3 index replacer if required by
   * the specification.
   *
   * @param specification the specification
   * @return the index replacer
   * @see NoIndexReplacer
   * @see OneIndexReplacer
   * @see TwoIndexReplacer
   * @see ThreeIndexReplacer
   */
  public static IndexReplacer createIndexReplacer(ProtocolSpecification specification) {
    return cache.computeIfAbsent(specification, IndexReplacerFactory::newReplacer);
  }

  /**
   * Create a new replacer.
   *
   * @param specification the specification
   * @return the index replacer
   */
  private static IndexReplacer newReplacer(ProtocolSpecification specification) {
    if (specification.getNumberOfIndexes() == 0) {
      return new NoIndexReplacer(specification);
    }
    if (specification.getNumberOfIndexes() == ONE) {
      return new OneIndexReplacer(specification);
    }
    if (specification.getNumberOfIndexes() == TWO) {
      return new TwoIndexReplacer(specification);
    }
    // The API does not support more than 3 indexes
    return new ThreeIndexReplacer(specification);
  }
}
