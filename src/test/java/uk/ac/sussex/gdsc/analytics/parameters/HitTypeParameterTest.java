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
import uk.ac.sussex.gdsc.analytics.parameters.HitTypeParameter;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class HitTypeParameterTest {
  @Test
  public void testToString() {
    for (final HitTypeParameter ht : HitTypeParameter.values()) {
      String expected = String.valueOf(ht).toLowerCase(Locale.getDefault());
      Assertions.assertEquals(expected, ht.toString());
    }
  }

  @Test
  public void testFormat() {
    Assertions.assertEquals("&ht=event", TestUtils.callAppendTo(HitTypeParameter.EVENT));
    Assertions.assertEquals("&ht=exception", TestUtils.callAppendTo(HitTypeParameter.EXCEPTION));
    Assertions.assertEquals("&ht=item", TestUtils.callAppendTo(HitTypeParameter.ITEM));
    Assertions.assertEquals("&ht=pageview", TestUtils.callAppendTo(HitTypeParameter.PAGEVIEW));
    Assertions.assertEquals("&ht=screenview", TestUtils.callAppendTo(HitTypeParameter.SCREENVIEW));
    Assertions.assertEquals("&ht=social", TestUtils.callAppendTo(HitTypeParameter.SOCIAL));
    Assertions.assertEquals("&ht=timing", TestUtils.callAppendTo(HitTypeParameter.TIMING));
    Assertions.assertEquals("&ht=transaction",
        TestUtils.callAppendTo(HitTypeParameter.TRANSACTION));
  }
}
