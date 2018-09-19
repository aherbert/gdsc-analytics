/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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

import java.util.regex.Pattern;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class QueueTimeParameterTest {
  @Test
  public void testFormat() {
    final long timestamp = System.currentTimeMillis();
    final UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    final Pattern pattern = Pattern.compile("^qt=-?[0-9]+$");
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) {
      final long stamp = timestamp - rg.nextInt(1000);
      final QueueTimeParameter qt = new QueueTimeParameter(stamp);
      final String s = qt.format();
      Assertions.assertTrue(pattern.matcher(s).matches(), s);
      Assertions.assertEquals(stamp, qt.getTimestamp());
      sb.setLength(0);
      QueueTimeParameter.appendTo(sb, timestamp);
      Assertions.assertTrue(pattern.matcher(sb).matches(), s);
    }
  }
}
