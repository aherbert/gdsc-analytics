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

package uk.ac.sussex.gdsc.analytics;

/**
 * Represent a session.
 */
public class Session {

  /** Google sessions timeout after 30 minutes of inactivity. */
  private long timeout = 30 * 60;
  /**
   * Timestamp of the session.
   */
  private long now;

  /**
   * Create a new session.
   */
  public Session() {
    reset();
  }

  /**
   * Get the number of seconds since the epoch (midnight, January 1, 1970 UTC).
   *
   * @return The timestamp in seconds
   */
  public static long timestamp() {
    return System.currentTimeMillis() / 1000L;
  }

  /**
   * Check if the session is new (i.e. has not been initialised, has timed out, or been reset).
   * Calling this refreshes the current session to prevent timeout.
   *
   * @return True if the session is new
   */
  public boolean isNew() {
    final long expires = getExpires();
    // Get the current time.
    now = timestamp();
    // Check if the session has expired
    return (now >= expires);
  }

  /**
   * Get the current session expire time.
   *
   * <p>This is the time returned from {@link #timestamp()} on from the last refresh plus the
   * session timeout.
   *
   * <p>If negative then the session has been reset and is currently a new session (with no
   * timeout).
   *
   * @return the expires
   */
  public long getExpires() {
    return now + timeout;
  }

  /**
   * Reset and start a new session.
   */
  public void reset() {
    now = Long.MIN_VALUE;
  }

  /**
   * Gets the timeout.
   *
   * @return the timeout
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * Sets the timeout.
   *
   * @param timeout the timeout to set
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
