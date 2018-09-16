/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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

import uk.ac.sussex.gdsc.analytics.TestUtils;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class NoIndexCurrencyParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexCurrencyParameter(TestUtils.newCurrencyParameterSpecification("Test"), null, 0);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexCurrencyParameter(ProtocolSpecification.TRANSACTION_REVENUE, null, 0);
    });

    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexCurrencyParameter((ProtocolSpecification) null, Locale.getDefault(), 0);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new NoIndexCurrencyParameter((ParameterSpecification) null, Locale.getDefault(), 0);
    });
  }

  @Test
  public void testFormat() {
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (final Locale locale : new Locale[] {Locale.getDefault(), Locale.FRANCE}) {
      for (int i = 0; i < 5; i++) {
        final String name = TestUtils.randomName(rg, 3);
        final double value = rg.nextDouble();

        final NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        final String valueString = formatter.format(value);

        NoIndexCurrencyParameter param = new NoIndexCurrencyParameter(
            TestUtils.newCurrencyParameterSpecification(name), locale, value);
        Assertions.assertEquals(String.format("%s=%s", name, valueString), param.format());
        Assertions.assertEquals(locale, param.getLocale());
        Assertions.assertEquals(value, param.getValue());

        param =
            new NoIndexCurrencyParameter(ProtocolSpecification.TRANSACTION_REVENUE, locale, value);
        Assertions.assertEquals(String.format("tr=%s", valueString), param.format());
        Assertions.assertEquals(value, param.getValue());
      }
    }
  }
}
