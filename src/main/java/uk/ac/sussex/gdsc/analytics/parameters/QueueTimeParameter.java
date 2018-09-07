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
public class QueueTimeParameter extends NoIndexParameter {

  /** The timestamp. */
  private final long timestamp;

  /**
   * Instantiates a new queue time parameter using the provide timestamp to mark when the hit
   * occurred.
   *
   * @param timestamp the timestamp
   */
  public QueueTimeParameter(long timestamp) {
    super(Parameter.QUEUE_TIME);
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
   */
  public static void appendTo(StringBuilder sb, long timestamp) {
    sb.append(Parameter.QUEUE_TIME.getNameFormat()).append(ParameterUtils.EQUAL)
        .append(System.currentTimeMillis() - timestamp);
  }
}
