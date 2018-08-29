/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics Measurement
 * protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert, Daniel Murphy
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
package uk.ac.sussex.gdsc.analytics;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import uk.ac.sussex.gdsc.analytics.Parameters.CustomDimension;
import uk.ac.sussex.gdsc.analytics.Parameters.CustomMetric;

@SuppressWarnings("javadoc")
public class ParametersTest
{
	@Test
	public void testCustomDimensions()
	{
		Parameters p = new Parameters();

		Assertions.assertFalse(p.hasCustomDimensions());
		Assertions.assertNull(p.getCustomDimensions());
		Assertions.assertEquals(0, p.getNumberOfCustomDimensions());

		int index = 3;
		String value = "33";

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
		List<CustomDimension> list = p.getCustomDimensions();
		Assertions.assertNotNull(list);
		Assertions.assertEquals(1, list.size());
		CustomDimension d = list.get(0);
		Assertions.assertEquals(index, d.index);
		Assertions.assertEquals(value, d.value);
	}

	@Test
	public void testCustomMetrics()
	{
		Parameters p = new Parameters();

		Assertions.assertFalse(p.hasCustomMetrics());
		Assertions.assertNull(p.getCustomMetrics());
		Assertions.assertEquals(0, p.getNumberOfCustomMetrics());

		int index = 3;
		int value = 33;

		// Ignored
		p.addCustomMetric(0, value);
		Assertions.assertFalse(p.hasCustomMetrics());
		p.addCustomMetric(201, value);
		Assertions.assertFalse(p.hasCustomMetrics());

		p.addCustomMetric(index, value);
		Assertions.assertTrue(p.hasCustomMetrics());
		Assertions.assertEquals(1, p.getNumberOfCustomMetrics());
		List<CustomMetric> list = p.getCustomMetrics();
		Assertions.assertNotNull(list);
		Assertions.assertEquals(1, list.size());
		CustomMetric d = list.get(0);
		Assertions.assertEquals(index, d.index);
		Assertions.assertEquals(value, d.value);
	}
}