/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2020 Alex Herbert
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
  ProductAction(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
