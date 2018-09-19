/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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

import java.util.EnumMap;
import java.util.Objects;

/**
 * Implements the {@link FormattedParameter} interface for {@link ProductAction}.
 *
 * @see <a href="http://goo.gl/a8d4RP#pa">Product Action</a>
 */
public final class ProductActionParameter implements FormattedParameter {

  /** Cache parameters for all the enum values. */
  private static final EnumMap<ProductAction, ProductActionParameter> cache =
      new EnumMap<>(ProductAction.class);

  /** The detail product action parameter. */
  public static final ProductActionParameter DETAIL;
  /** The click product action parameter. */
  public static final ProductActionParameter CLICK;
  /** The add product action parameter. */
  public static final ProductActionParameter ADD;
  /** The remove product action parameter. */
  public static final ProductActionParameter REMOVE;
  /** The checkout product action parameter. */
  public static final ProductActionParameter CHECKOUT;
  /** The checkout option product action parameter. */
  public static final ProductActionParameter CHECKOUT_OPTION;
  /** The purchase product action parameter. */
  public static final ProductActionParameter PURCHASE;
  /** The refund product action parameter. */
  public static final ProductActionParameter REFUND;

  static {
    DETAIL = new ProductActionParameter(ProductAction.DETAIL);
    CLICK = new ProductActionParameter(ProductAction.CLICK);
    ADD = new ProductActionParameter(ProductAction.ADD);
    REMOVE = new ProductActionParameter(ProductAction.REMOVE);
    CHECKOUT = new ProductActionParameter(ProductAction.CHECKOUT);
    CHECKOUT_OPTION = new ProductActionParameter(ProductAction.CHECKOUT_OPTION);
    PURCHASE = new ProductActionParameter(ProductAction.PURCHASE);
    REFUND = new ProductActionParameter(ProductAction.REFUND);
  }

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
    // @formatter:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.PRODUCT_ACTION.getNameFormat())
        .append(Constants.EQUAL)
        .append(productAction.toString());
    // @formatter:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }

  /**
   * Creates the product action parameter.
   *
   * @param productAction the product action
   * @return the product action parameter
   */
  public static ProductActionParameter create(ProductAction productAction) {
    Objects.requireNonNull(productAction, "Product action is null");
    return cache.computeIfAbsent(productAction, ProductActionParameter::new);
  }
}
