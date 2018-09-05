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
 * Adds parameter {@code v=1} to a URL.
 * 
 * @see <a href="http://goo.gl/a8d4RP#v">Protocol Version</a>
 */
public final class Version1Parameter implements FormattedParameter {

  /** The default instance. */
  private static final Version1Parameter DEFAULT_INSTANCE = new Version1Parameter();
  /**
   * The version characters. Used as a alternative to {@link StringBuilder#append(String)} to avoid
   * the lengths checks within that method.
   */
  private static final char[] VERSION = "&v=1".toCharArray();

  /**
   * No public constructor.
   */
  private Version1Parameter() {}

  /**
   * Gets the default instance.
   *
   * @return the default instance
   */
  public static Version1Parameter getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @Override
  public void appendTo(StringBuilder sb) {
    sb.append(VERSION);
  }
}
