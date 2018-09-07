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
 * Implements the {@link FormattedParameter} interface for {@link SessionControl}.
 * 
 * @see <a href="http://goo.gl/a8d4RP#pa">Product Action</a>
 */
public class ProductActionParameter implements FormattedParameter {
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
    final StringBuilder sb = new StringBuilder();
    sb.append(ProtocolSpecification.PRODUCT_ACTION.getNameFormatRef());
    sb.append(Constants.EQUAL);
    sb.append(productAction.toString());
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }
}
