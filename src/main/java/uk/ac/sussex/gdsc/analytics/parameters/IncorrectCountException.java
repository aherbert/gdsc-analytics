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
 * Thrown when the expected count did not match the observed count.
 */
public class IncorrectCountException extends RuntimeException {

  /**
   * The serial version ID.
   */
  private static final long serialVersionUID = -4676291340013989132L;

  /** The index count. */
  private final int expected;

  /** The required index count. */
  private final int observed;

  /** The detail message. */
  private final String detailMessage;

  /**
   * Instantiates a new incorrect index count exception.
   *
   * @param expected the expected value
   * @param observed the observed value
   */
  public IncorrectCountException(int expected, int observed) {
    this(expected, observed, null);
  }

  /**
   * Instantiates a new incorrect index count exception.
   *
   * @param expected the expected value
   * @param observed the observed value
   * @param detailMessage the detail message
   */
  public IncorrectCountException(int expected, int observed, String detailMessage) {
    this.expected = expected;
    this.observed = observed;
    this.detailMessage = detailMessage;
  }

  /**
   * Gets the expected.
   *
   * @return the expected
   */
  public int getExpected() {
    return expected;
  }

  /**
   * Gets the observed.
   *
   * @return the observed
   */
  public int getObserved() {
    return observed;
  }

  @Override
  public String getMessage() {
    //@formatter:off
    final StringBuilder sb = new StringBuilder(
        "Incorrect count: expected <").append(expected).append('>')
        .append("observed <").append(observed).append('>');
    //@formatter:on
    if (ParameterUtils.isNotEmpty(detailMessage)) {
      sb.append(": ").append(detailMessage);
    }
    return sb.toString();
  }
}
