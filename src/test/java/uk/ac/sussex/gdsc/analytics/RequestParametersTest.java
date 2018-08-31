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
