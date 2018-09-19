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

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class FormatterParameterTest {

  // Test the default methods

  @Test
  public void testAppendTo() {
    // When nothing is added
    {
      final FormattedParameter fp = (sb) -> sb;
      final StringBuilder sb = new StringBuilder();
      Assertions.assertEquals("", fp.appendTo(sb).toString());
      Assertions.assertEquals("", fp.appendTo(sb).toString());
    }

    // When something is added
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      final FormattedParameter fp = (sb) -> sb.append(name);
      final StringBuilder sb = new StringBuilder();
      Assertions.assertEquals(name, fp.appendTo(sb).toString());
      // When something is added an '&' character is used to separate them
      Assertions.assertEquals(name + "&" + name, fp.appendTo(sb).toString());
    }
  }

  @Test
  public void testFormat() {
    // With empty
    FormattedParameter fp = (sb) -> sb;
    Assertions.assertEquals("", fp.format());

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      fp = (sb) -> sb.append(name);
      Assertions.assertEquals(name, fp.format());
    }
  }

  @Test
  public void testFreeze() {
    // With empty
    FormattedParameter fp = (sb) -> sb;
    FormattedParameter fp2 = fp.freeze();
    Assertions.assertNotSame(fp, fp2);
    Assertions.assertEquals("", callFormatTo(fp2));
    Assertions.assertEquals("", fp2.format());
    Assertions.assertSame(fp2, fp2.freeze());

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      fp = (sb) -> sb.append(name);
      final String appendToString = callFormatTo(fp);
      final String formatString = fp.format();
      Assertions.assertEquals(name, fp.format());
      fp2 = fp.freeze();
      Assertions.assertNotSame(fp, fp2);
      Assertions.assertEquals(appendToString, callFormatTo(fp2));
      Assertions.assertEquals(formatString, fp2.format());
      Assertions.assertSame(fp2, fp2.freeze());
    }
  }

  @Test
  public void testEmpty() {
    final FormattedParameter fp = FormattedParameter.empty();
    Assertions.assertEquals("", callFormatTo(fp));
    Assertions.assertEquals("", fp.format());
    final String text = "not-empty";
    StringBuilder sb = new StringBuilder(text);
    Assertions.assertSame(sb, fp.appendTo(sb));
    Assertions.assertEquals(text, sb.toString());
    Assertions.assertSame(fp, fp.freeze());
    final FormattedParameter fp2 = FormattedParameter.empty();
    Assertions.assertNotSame(fp, fp2);
  }

  /**
   * Call {@link FormattedParameter#formatTo(StringBuilder)} and return the string from a fresh
   * StringBuilder.
   *
   * <p>This is
   *
   * @param parameter the parameter
   * @return the string
   */
  public static String callFormatTo(FormattedParameter parameter) {
    final StringBuilder sb = new StringBuilder();
    parameter.formatTo(sb);
    return sb.toString();
  }
}
