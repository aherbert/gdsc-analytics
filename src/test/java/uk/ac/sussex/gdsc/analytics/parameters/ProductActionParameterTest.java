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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ProductActionParameterTest {
  @Test
  public void testToString() {
    int count = 0;
    Assertions.assertEquals("pa=add", ProductActionParameter.ADD.format());
    count++;
    Assertions.assertEquals("pa=checkout", ProductActionParameter.CHECKOUT.format());
    count++;
    Assertions.assertEquals("pa=checkout_option", ProductActionParameter.CHECKOUT_OPTION.format());
    count++;
    Assertions.assertEquals("pa=click", ProductActionParameter.CLICK.format());
    count++;
    Assertions.assertEquals("pa=detail", ProductActionParameter.DETAIL.format());
    count++;
    Assertions.assertEquals("pa=purchase", ProductActionParameter.PURCHASE.format());
    count++;
    Assertions.assertEquals("pa=refund", ProductActionParameter.REFUND.format());
    count++;
    Assertions.assertEquals("pa=remove", ProductActionParameter.REMOVE.format());
    count++;
    // Make sure we cover all the values
    Assertions.assertEquals(ProductAction.values().length, count);
  }
}