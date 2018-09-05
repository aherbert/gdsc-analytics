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

/**
 * Stores the queue time parameter.
 * 
 * <p>Note that in the case of asynchronous hits it is better to add the queue time just before
 * sending the tracking request using the helper method {@link #appendTo(StringBuilder, long)}.
 * 
 * @see <a href="http://goo.gl/a8d4RP#qt">Queue Time</a>
 */
public class QueueTimeParameter implements FormattedParameter {

  /**
   * The name prefix characters. Used as a alternative to {@link StringBuilder#append(String)} to
   * avoid the lengths checks within that method.
   */
  private static final char[] PREFIX = "&qt=".toCharArray();

  /** The value. */
  private final long value;

  /**
   * Instantiates a new instance.
   *
   * @param value the value
   */
  public QueueTimeParameter(long value) {
    this.value = value;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public final long getValue() {
    return value;
  }

  @Override
  public void appendTo(StringBuilder sb) {
    sb.append(PREFIX).append(value);
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
   */
  public static void appendTo(StringBuilder sb, long timestamp) {
    sb.append(PREFIX).append(System.currentTimeMillis() - timestamp);
  }
}
