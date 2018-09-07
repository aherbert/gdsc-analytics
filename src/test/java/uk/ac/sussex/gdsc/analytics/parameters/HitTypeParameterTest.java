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

import uk.ac.sussex.gdsc.analytics.TestUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class HitTypeParameterTest {

  @Test
  public void testFormat() {
    Assertions.assertEquals("&ht=event", TestUtils.callFormatTo(HitTypeParameter.EVENT));
    Assertions.assertEquals("&ht=exception", TestUtils.callFormatTo(HitTypeParameter.EXCEPTION));
    Assertions.assertEquals("&ht=item", TestUtils.callFormatTo(HitTypeParameter.ITEM));
    Assertions.assertEquals("&ht=pageview", TestUtils.callFormatTo(HitTypeParameter.PAGEVIEW));
    Assertions.assertEquals("&ht=screenview", TestUtils.callFormatTo(HitTypeParameter.SCREENVIEW));
    Assertions.assertEquals("&ht=social", TestUtils.callFormatTo(HitTypeParameter.SOCIAL));
    Assertions.assertEquals("&ht=timing", TestUtils.callFormatTo(HitTypeParameter.TIMING));
    Assertions.assertEquals("&ht=transaction",
        TestUtils.callFormatTo(HitTypeParameter.TRANSACTION));
  }
}
