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

package uk.ac.sussex.gdsc.analytics;

import uk.ac.sussex.gdsc.analytics.Parameters.CustomDimension;
import uk.ac.sussex.gdsc.analytics.Parameters.CustomMetric;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ParametersTest {
  @Test
  public void testCustomDimensions() {
    final Parameters p = new Parameters();

    Assertions.assertFalse(p.hasCustomDimensions());
    Assertions.assertNull(p.getCustomDimensions());
    Assertions.assertEquals(0, p.getNumberOfCustomDimensions());

    final int index = 3;
    final String value = "33";

    // Ignored
    p.addCustomDimension(index, null);
    Assertions.assertFalse(p.hasCustomDimensions());
    p.addCustomDimension(0, value);
    Assertions.assertFalse(p.hasCustomDimensions());
    p.addCustomDimension(201, value);
    Assertions.assertFalse(p.hasCustomDimensions());

    p.addCustomDimension(index, value);
    Assertions.assertTrue(p.hasCustomDimensions());
    Assertions.assertEquals(1, p.getNumberOfCustomDimensions());
    final List<CustomDimension> list = p.getCustomDimensions();
    Assertions.assertNotNull(list);
    Assertions.assertEquals(1, list.size());
    final CustomDimension d = list.get(0);
    Assertions.assertEquals(index, d.index);
    Assertions.assertEquals(value, d.value);
  }

  @Test
  public void testCustomMetrics() {
    final Parameters p = new Parameters();

    Assertions.assertFalse(p.hasCustomMetrics());
    Assertions.assertNull(p.getCustomMetrics());
    Assertions.assertEquals(0, p.getNumberOfCustomMetrics());

    final int index = 3;
    final int value = 33;

    // Ignored
    p.addCustomMetric(0, value);
    Assertions.assertFalse(p.hasCustomMetrics());
    p.addCustomMetric(201, value);
    Assertions.assertFalse(p.hasCustomMetrics());

    p.addCustomMetric(index, value);
    Assertions.assertTrue(p.hasCustomMetrics());
    Assertions.assertEquals(1, p.getNumberOfCustomMetrics());
    final List<CustomMetric> list = p.getCustomMetrics();
    Assertions.assertNotNull(list);
    Assertions.assertEquals(1, list.size());
    final CustomMetric d = list.get(0);
    Assertions.assertEquals(index, d.index);
    Assertions.assertEquals(value, d.value);
  }
}
