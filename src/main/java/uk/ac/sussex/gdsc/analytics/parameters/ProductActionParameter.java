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

package uk.ac.sussex.gdsc.analytics.parameters;

/**
 * Implements the {@link FormattedParameter} interface for {@link SessionControl}.
 *
 * @see <a href="http://goo.gl/a8d4RP#pa">Product Action</a>
 */
public final class ProductActionParameter implements FormattedParameter {
  /** The detail product action parameter. */
  public static final ProductActionParameter DETAIL =
      new ProductActionParameter(ProductAction.DETAIL);
  /** The click product action parameter. */
  public static final ProductActionParameter CLICK =
      new ProductActionParameter(ProductAction.CLICK);
  /** The add product action parameter. */
  public static final ProductActionParameter ADD = new ProductActionParameter(ProductAction.ADD);
  /** The remove product action parameter. */
  public static final ProductActionParameter REMOVE =
      new ProductActionParameter(ProductAction.REMOVE);
  /** The checkout product action parameter. */
  public static final ProductActionParameter CHECKOUT =
      new ProductActionParameter(ProductAction.CHECKOUT);
  /** The checkout option product action parameter. */
  public static final ProductActionParameter CHECKOUT_OPTION =
      new ProductActionParameter(ProductAction.CHECKOUT_OPTION);
  /** The purchase product action parameter. */
  public static final ProductActionParameter PURCHASE =
      new ProductActionParameter(ProductAction.PURCHASE);
  /** The refund product action parameter. */
  public static final ProductActionParameter REFUND =
      new ProductActionParameter(ProductAction.REFUND);

  /**
   * The formatted parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] chars;

  /**
   * Creates a new instance.
   *
   * @param productAction the product action
   */
  private ProductActionParameter(ProductAction productAction) {
    //@formmater:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.PRODUCT_ACTION.getNameFormat())
        .append(Constants.EQUAL)
        .append(productAction.toString());
    //@formmater:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }
}
