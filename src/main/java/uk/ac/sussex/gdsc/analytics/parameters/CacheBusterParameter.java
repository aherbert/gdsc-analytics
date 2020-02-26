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

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

/**
 * Adds a cache buster parameter when using the HTTP GET method.
 *
 * <p>This should be the last parameter in a URL.
 *
 * @see <a href="http://goo.gl/a8d4RP#z">Cache Buster</a>
 */
public class CacheBusterParameter extends NoIndexParameter {

  /** The default instance. */
  private static final CacheBusterParameter DEFAULT_INSTANCE = new CacheBusterParameter();

  /** The random appender. */
  private final UnaryOperator<StringBuilder> randomAppender;

  /**
   * Creates a new instance with the provided operator to add random text to the StringBuilder.
   *
   * @param randomAppender the random appender
   */
  public CacheBusterParameter(UnaryOperator<StringBuilder> randomAppender) {
    super(ProtocolSpecification.CACHE_BUSTER);
    // This is here in case the API changes.
    // This class assumes it can be anything.
    ParameterUtils.compatibleValueType(ValueType.TEXT, ProtocolSpecification.CACHE_BUSTER);
    this.randomAppender = Objects.requireNonNull(randomAppender, "Random appender");
  }

  /**
   * Creates a new instance with a random integer for the cache buster.
   */
  private CacheBusterParameter() {
    this(CacheBusterParameter::addRandomNumber);
  }

  /**
   * Adds random number to the string builder.
   *
   * @param sb the string builder
   * @return the string builder
   */
  public static StringBuilder addRandomNumber(StringBuilder sb) {
    return sb.append(ThreadLocalRandom.current().nextInt());
  }

  /**
   * Gets a default instance.
   *
   * <p>The class is thread safe as it uses {@link ThreadLocalRandom} to generate the random an
   * integer to append as the cache buster.
   *
   * @return the default instance
   */
  public static CacheBusterParameter getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    // These can be chained but it is more readable in sequence
    appendNameEquals(sb);
    return randomAppender.apply(sb);
  }
}
