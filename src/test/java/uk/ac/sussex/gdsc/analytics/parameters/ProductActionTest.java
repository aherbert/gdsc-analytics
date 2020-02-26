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
