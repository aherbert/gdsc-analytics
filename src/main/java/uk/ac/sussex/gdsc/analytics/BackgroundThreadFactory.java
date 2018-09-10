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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A factory for creating background threads.
 *
 * <p>This is a simple wrapper around {@link Executors#defaultThreadFactory()} to set the thread
 * priority to a configured level and Daemon flag to {@code true}.
 */
class BackgroundThreadFactory implements ThreadFactory {

  /** The thread factory to delegate the work to. */
  private final ThreadFactory threadFactory = Executors.defaultThreadFactory();

  /** The thread priority. */
  private final int priority;

  /**
   * Instantiates a new background thread factory.
   *
   * @param priority the priority
   * @exception IllegalArgumentException If the priority is not in the correct range.
   * @see Thread#setPriority(int)
   */
  BackgroundThreadFactory(int priority) {
    ArgumentUtils.validateThreadPriority(priority);
    this.priority = priority;
  }

  @Override
  public Thread newThread(Runnable runnable) {
    final Thread t = threadFactory.newThread(runnable);
    t.setDaemon(true);
    t.setPriority(priority);
    return t;
  }
}
