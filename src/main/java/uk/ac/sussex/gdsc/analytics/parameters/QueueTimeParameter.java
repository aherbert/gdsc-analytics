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
 * Stores the queue time parameter.
 *
 * <p>Note that in the case of asynchronous hits it is better to add the queue time just before
 * sending the tracking request using the helper method {@link #appendTo(StringBuilder, long)}.
 *
 * @see <a href="http://goo.gl/a8d4RP#qt">Queue Time</a>
 */
public class QueueTimeParameter extends NoIndexParameter {

  /** The timestamp. */
  private final long timestamp;

  /**
   * Creates a new instance using the provided timestamp to mark when the hit occurred.
   *
   * @param timestamp the timestamp
   */
  public QueueTimeParameter(long timestamp) {
    super(ProtocolSpecification.QUEUE_TIME);
    this.timestamp = timestamp;
  }

  /**
   * Gets the timestamp when the hit occurred.
   *
   * @return the timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return appendNameEquals(sb).append(System.currentTimeMillis() - timestamp);
  }

  /**
   * Append the queue time parameter to the provided {@link StringBuilder}.
   * 
   * <p>The queue time parameter will be equal to {@code System.currentTimeMillis() - timestamp}.
   * 
   * <p>This method should be used immediately prior to sending the hit, e.g. when constructing a
   * hit URL asynchronously.
   *
   * @param sb the string builder
   * @param timestamp the timestamp when the hit occurred (in milliseconds)
   * @return the string builder
   */
  public static StringBuilder appendTo(StringBuilder sb, long timestamp) {
    return ParameterUtils.appendAndIfNotEmpty(sb)
        .append(ProtocolSpecification.QUEUE_TIME.getNameFormat()).append(Constants.EQUAL)
        .append(System.currentTimeMillis() - timestamp);
  }
}
