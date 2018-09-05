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
