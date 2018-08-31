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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class RequestParametersTest {
    @Test
    public void testConstructor() {
        for (final HitType ht : HitType.values()) {
            final RequestParameters rp = new RequestParameters(ht);
            Assertions.assertEquals(ht, rp.getHitTypeEnum());
            Assertions.assertEquals(ht.toString(), rp.getHitType());
        }
    }

    /**
     * Test all properties invalidate the URL
     */
    @Test
    public void testProperties() {
        final RequestParameters rp = new RequestParameters(HitType.PAGEVIEW);

        final String documentPath = "1";
        final String documentTitle = "2";
        final String category = "3";
        final String action = "4";
        final String label = "5";
        final Integer value = 99;

        Assertions.assertNull(rp.getDocumentPath());
        Assertions.assertNull(rp.getDocumentTitle());
        Assertions.assertNull(rp.getCategory());
        Assertions.assertNull(rp.getAction());
        Assertions.assertNull(rp.getLabel());
        Assertions.assertNull(rp.getValue());

        rp.setDocumentPath(documentPath);
        Assertions.assertEquals(documentPath, rp.getDocumentPath());
        rp.setDocumentTitle(documentTitle);
        Assertions.assertEquals(documentTitle, rp.getDocumentTitle());
        rp.setCategory(category);
        Assertions.assertEquals(category, rp.getCategory());
        rp.setAction(action);
        Assertions.assertEquals(action, rp.getAction());
        rp.setLabel(label);
        Assertions.assertEquals(label, rp.getLabel());
        rp.setValue(value);
        Assertions.assertEquals(value, rp.getValue());

        Assertions.assertFalse(rp.hasCustomDimensions());
        rp.addCustomDimension(1, null); // Ignored
        Assertions.assertFalse(rp.hasCustomDimensions());
        rp.addCustomDimension(3, "33");
        Assertions.assertTrue(rp.hasCustomDimensions());

        Assertions.assertFalse(rp.hasCustomMetrics());
        rp.addCustomMetric(4, 44);
        Assertions.assertTrue(rp.hasCustomMetrics());
    }
}
