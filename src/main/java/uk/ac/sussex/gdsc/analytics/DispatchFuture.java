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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A dummy class implementing the {@link Future} interface to return a status result.
 */
public class DispatchFuture implements Future<DispatchStatus> {

  /** The status. */
  private final DispatchStatus status;

  /**
   * Instantiates a new dispatch future.
   *
   * @param status the status
   */
  DispatchFuture(DispatchStatus status) {
    this.status = status;
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public DispatchStatus getStatus() {
    return status;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#cancel(boolean)
   */
  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#isCancelled()
   */
  @Override
  public boolean isCancelled() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#isDone()
   */
  @Override
  public boolean isDone() {
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#get()
   */
  @Override
  public DispatchStatus get() {
    return getStatus();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
   */
  @Override
  public DispatchStatus get(long timeout, TimeUnit unit) {
    return getStatus();
  }
}
