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
 * Defines product action values for the Google Analytics Measurement Protocol.
 * 
 * @see <a href="http://goo.gl/a8d4RP#pa">Product Action</a>
 */
public enum ProductAction {
  /** The detail product action. */
  DETAIL("detail"),
  /** The click product action. */
  CLICK("click"),
  /** The add product action. */
  ADD("add"),
  /** The remove product action. */
  REMOVE("remove"),
  /** The checkout product action. */
  CHECKOUT("checkout"),
  /** The checkout option product action. */
  CHECKOUT_OPTION("checkout_option"),
  /** The purchase product action. */
  PURCHASE("purchase"),
  /** The refund product action. */
  REFUND("refund");

  /** The name. */
  private final String name;

  /**
   * Instantiates a new product action.
   *
   * @param name the name
   */
  private ProductAction(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }
}
