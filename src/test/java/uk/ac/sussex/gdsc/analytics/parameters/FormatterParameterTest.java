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
  @Test
  public void testToPostString() {
    // With empty
    FormattedParameter fp = (sb) -> {
      // Do nothing
    };
    Assertions.assertEquals("", fp.toPostString());

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      fp = (sb) -> sb.append('&').append(name);
      Assertions.assertEquals(name, fp.toPostString());
    }
  }

  @Test
  public void testToGetString() {
    // With empty
    FormattedParameter fp = (sb) -> {
      // Do nothing
    };
    Assertions.assertEquals("", fp.toGetString());

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      fp = (sb) -> sb.append('&').append(name);
      Assertions.assertEquals("?" + name, fp.toGetString());
    }
  }

  @Test
  public void testSimplify() {
    // With empty
    FormattedParameter fp = (sb) -> {
      // Do nothing
    };
    FormattedParameter fp2 = fp.simplify();
    Assertions.assertNotSame(fp, fp2);
    Assertions.assertEquals("", TestUtils.callAppendTo(fp2));
    Assertions.assertEquals("", fp2.toPostString());
    Assertions.assertEquals("", fp2.toGetString());
    Assertions.assertSame(fp2, fp2.simplify());

    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    for (int i = 0; i < 5; i++) {
      final String name = TestUtils.randomName(rg, 10);
      fp = (sb) -> sb.append('&').append(name);
      String appendTo = TestUtils.callAppendTo(fp);
      String postString = fp.toPostString();
      String getString = fp.toGetString();
      Assertions.assertEquals(name, postString);
      Assertions.assertEquals("?" + name, getString);
      fp2 = fp.simplify();
      Assertions.assertNotSame(fp, fp2);
      Assertions.assertEquals(appendTo, TestUtils.callAppendTo(fp2));
      Assertions.assertEquals(postString, fp2.toPostString());
      Assertions.assertEquals(getString, fp2.toGetString());
      Assertions.assertSame(fp2, fp2.simplify());
    }
  }

  @Test
  public void testEmpty() {
    FormattedParameter fp = FormattedParameter.empty();
    Assertions.assertEquals("", TestUtils.callAppendTo(fp));
    Assertions.assertEquals("", fp.toPostString());
    Assertions.assertEquals("", fp.toGetString());
    Assertions.assertSame(fp, fp.simplify());
    FormattedParameter fp2 = FormattedParameter.empty();
    Assertions.assertNotSame(fp, fp2);
  }
}
