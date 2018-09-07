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
 * Contains constants.
 */
public final class Constants {

  /**
   * The '<strong>{@code =}</strong>' (Equal) character.
   * 
   * <p>Used to to create a {@code name=value} pair for a URL.
   */
  public static final char EQUAL = '=';

  /**
   * The '<strong>{@code &}</strong>' (Ampersand) character.
   * 
   * <p>Used to join {@code name=value} pairs for a URL, e.g. {@code name=value&name2=value2}.
   */
  public static final char AND = '&';

  /**
   * The '<strong>{@code _}</strong>' (Underscore) character.
   * 
   * <p>The character used to identify an index within the name format for the {@code name=value}
   * parameter pair, e.g. {@code cm_} where {@code _} is the index of the custom metric {@code cm}
   * parameter.
   */
  public static final char UNDERSCORE = '_';

  /**
   * No public construction.
   */
  private Constants() {
    // Do nothing
  }
}
