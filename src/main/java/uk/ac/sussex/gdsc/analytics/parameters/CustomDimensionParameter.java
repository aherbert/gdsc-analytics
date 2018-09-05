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
 * Stores a custom dimension parameter.
 * 
 * @see <a
 *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cd_">Custom
 *      Dimension</a>
 */
public final class CustomDimensionParameter extends IndexedStringParameter {

  /**
   * Instantiates a new custom dimension.
   *
   * @param index the index
   * @param value the value
   * @throws IllegalArgumentException If the index is not strictly positive
   */
  public CustomDimensionParameter(int index, String value) throws IllegalArgumentException {
    super("&cd", index, value);
  }
}
