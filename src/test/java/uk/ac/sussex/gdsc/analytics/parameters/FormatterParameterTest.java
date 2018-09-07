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
      FormattedParameter fp = (sb) -> sb;
      StringBuilder sb = new StringBuilder();
      Assertions.assertEquals("", fp.appendTo2(sb).toString());
      Assertions.assertEquals("", fp.appendTo2(sb).toString());
    }

    // When something is added
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      FormattedParameter fp = (sb) -> sb.append(name);
      StringBuilder sb = new StringBuilder();
      Assertions.assertEquals(name, fp.appendTo2(sb).toString());
      // When something is added an '&' character is used to separate them
      Assertions.assertEquals(name + "&" + name, fp.appendTo2(sb).toString());
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
      String appendToString = callFormatTo(fp);
      String formatString = fp.format();
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
    FormattedParameter fp = FormattedParameter.empty();
    Assertions.assertEquals("", callFormatTo(fp));
    Assertions.assertEquals("", fp.format());
    Assertions.assertSame(fp, fp.freeze());
    FormattedParameter fp2 = FormattedParameter.empty();
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
