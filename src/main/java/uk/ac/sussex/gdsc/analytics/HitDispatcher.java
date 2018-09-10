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

import uk.ac.sussex.gdsc.analytics.parameters.FormattedParameter;

import java.io.IOException;

/**
 * Interface to allow a Google Analytics hit request to be sent.
 *
 * <p>Any errors should be caught and stored as an {@link IOException} to be retrieved using
 * {@link #getLastIoException()}.
 *
 * <p>The hit may be generated using {@link FormattedParameter#format()}.
 */
public interface HitDispatcher {

  /**
   * Send the hit to Google Analytics.
   *
   * <p>Note: The Google Analytics server does not return HTTP response error codes but the
   * validation server does provide response output.
   *
   * <p>The timestamp can be used to add a queue time parameter to the hit.
   *
   * <p>The callback should be called after the hit has been sent and can be used to interrogate the
   * response from the remote server.
   *
   * @param hit the hit
   * @param timestamp the timestamp when the hit occurred
   * @param callback the callback
   * @return the dispatch status
   * @see <a
   *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/validating-hits">Validating
   *      Hits</a>
   */
  DispatchStatus send(CharSequence hit, long timestamp, HttpUrlConnectionCallback callback);

  /**
   * Send the hit to Google Analytics.
   *
   * <p>Note: The Google Analytics server does not return HTTP response error codes but the
   * validation server does provide response output.
   *
   * <p>The timestamp can be used to add a queue time parameter to the hit.
   *
   * @param hit the hit
   * @param timestamp the timestamp when the hit occurred
   * @return the dispatch status
   * @see <a
   *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/validating-hits">Validating
   *      Hits</a>
   */
  default DispatchStatus send(CharSequence hit, long timestamp) {
    return send(hit, timestamp, null);
  }

  /**
   * The last IO exception that occurred when dispatching a request.
   *
   * @return the last IO exception
   */
  IOException getLastIoException();

  /**
   * Checks if the dispatcher is disabled.
   *
   * <p>This is may be set to true if an exception occurred during a dispatch request, i.e. any
   * subsequent tracking requests will fail.
   *
   * @return true, if is disabled
   * @see #getLastIoException()
   * @see #start()
   * @see #stop()
   */
  boolean isDisabled();

  /**
   * Start the dispatcher. This is expected to enable sending hit requests.
   *
   * <p>This may not be possible depending on the state.
   *
   * @return true, if started
   */
  boolean start();

  /**
   * Stop the dispatcher. This is expected to disable sending hit requests.
   *
   * @return true, if stopped
   */
  boolean stop();
}
