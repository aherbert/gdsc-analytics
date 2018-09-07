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
public class ProductActionTest {
  @Test
  public void testToString() {
    int count = 0;
    Assertions.assertEquals("add", ProductAction.ADD.toString());
    count++;
    Assertions.assertEquals("checkout", ProductAction.CHECKOUT.toString());
    count++;
    Assertions.assertEquals("checkout_option", ProductAction.CHECKOUT_OPTION.toString());
    count++;
    Assertions.assertEquals("click", ProductAction.CLICK.toString());
    count++;
    Assertions.assertEquals("detail", ProductAction.DETAIL.toString());
    count++;
    Assertions.assertEquals("purchase", ProductAction.PURCHASE.toString());
    count++;
    Assertions.assertEquals("refund", ProductAction.REFUND.toString());
    count++;
    Assertions.assertEquals("remove", ProductAction.REMOVE.toString());
    count++;
    // Make sure we cover all the values
    Assertions.assertEquals(ProductAction.values().length, count);
  }
}
