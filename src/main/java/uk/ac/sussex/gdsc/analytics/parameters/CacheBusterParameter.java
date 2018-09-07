/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
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
  private static final CacheBusterParameter defaultInstance = new CacheBusterParameter();

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
    return defaultInstance;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    // These can be chained but it is more readable in sequence
    appendNameEquals(sb);
    return randomAppender.apply(sb);
  }
}
