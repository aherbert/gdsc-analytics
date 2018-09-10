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

package uk.ac.sussex.gdsc.analytics;

import uk.ac.sussex.gdsc.analytics.parameters.ParameterUtils;

/**
 * Represent a session of activity.
 *
 * <p>The session is a state-machine where the state is either {@code new}, {@code current} or
 * {@code expired}.
 *
 * <p>Any activity should be registered with the session using the {@link #refresh()} method. This
 * will either create a {@code new} session or maintain the {@code current} session.
 *
 * <p>It is possible to determine if the session is {@code expired} without refreshing it by testing
 * the expire time using {@link #hasExpired()}.
 */
public class Session {

  /**
   * The default timeout in milliseconds.
   *
   * <p>Sessions timeout after 30 minutes of inactivity.
   */
  public static final long DEFAULT_TIMEOUT = 30 * 60000L;

  /**
   * Set to zero to ensure each interaction is a new session.
   */
  public static final long ALWAYS_TIMEOUT = 0L;

  /**
   * Set to Long.MAX_VALUE to ensure no timeout.
   */
  public static final long NEVER_TIMEOUT = Long.MAX_VALUE;

  /**
   * The timeout in milliseconds.
   */
  private long timeout = 30 * 60000L;
  /**
   * Timestamp of the session.
   */
  private long now;

  /**
   * Create a new session with the the {@link #DEFAULT_TIMEOUT}.
   */
  public Session() {
    this(DEFAULT_TIMEOUT);
  }

  /**
   * Create a new session.
   *
   * @param timeout the timeout
   * @throws IllegalArgumentException If the timeout is negative
   */
  public Session(long timeout) throws IllegalArgumentException {
    setTimeout(timeout);
    reset();
  }

  /**
   * Calling this refreshes the current session to prevent timeout.
   *
   * <p>Returns {@code true} if this changes the state of the session to {@code new}, i.e. has not
   * been initialised, has timed out, or been reset).
   *
   * <p>Returns {@code false} if this changes the state of the session to {@code current}, i.e. the
   * expire time has not been reached.
   *
   * <p>The timestamp of the interaction with the session can be obtained from
   * {@link #getTimeStamp()}.
   *
   * @return True if the session is new
   */
  public boolean refresh() {
    final long expires = getExpireTime();
    // Get the current time.
    now = System.currentTimeMillis();
    // Check if the session has expired
    return (now >= expires);
  }

  /**
   * Checks if the session has expired.
   *
   * <p>This does not refresh the session.
   *
   * @return true, if expired
   */
  public boolean hasExpired() {
    if (now == 0) {
      // New session
      return false;
    }
    return System.currentTimeMillis() >= getExpireTime();
  }

  /**
   * Get the timestamp of the last call to {@link #refresh()}.
   *
   * <p>If zero then the session has been reset and is currently a new session (with no timeout).
   *
   * @return the timestamp
   */
  public long getTimeStamp() {
    return now;
  }

  /**
   * Get the current session expire time.
   *
   * <p>This is the time returned from {@link #getTimeStamp()} plus the session timeout.
   *
   * <p>If zero then the session has been reset and is currently a new session (with no timeout).
   *
   * @return the expire time
   */
  public long getExpireTime() {
    if (now == 0) {
      return 0;
    }
    // Overflow sensitive
    final long expireTime = now + timeout;
    return (expireTime < 0) ? Long.MAX_VALUE : expireTime;
  }


  /**
   * Reset and start a new session.
   */
  public final void reset() {
    now = 0;
  }

  /**
   * Gets the timeout in milliseconds.
   *
   * @return the timeout
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * Sets the timeout in milliseconds.
   *
   * <p>Set to zero to ensure the session is always new (i.e. continuously timeout).
   *
   * @param timeout the timeout to set
   * @throws IllegalArgumentException If the timeout is negative
   */
  public void setTimeout(long timeout) throws IllegalArgumentException {
    this.timeout = ParameterUtils.requirePositive(timeout, "Timeout must be positive");
  }
}
