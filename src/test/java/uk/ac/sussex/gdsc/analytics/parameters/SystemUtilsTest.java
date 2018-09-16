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

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class SystemUtilsTest {
  @Test
  public void testIsWindows() {
    Assertions.assertTrue(SystemUtils.isWindows("Windows tjlatl"));
    Assertions.assertFalse(SystemUtils.isWindows("  Windows tjlatl"));
  }

  @Test
  public void testIsLinux() {
    Assertions.assertTrue(SystemUtils.isLinux("Linux tjlatl"));
    Assertions.assertFalse(SystemUtils.isLinux("  Linux tjlatl"));
  }

  @Test
  public void testGetScreenSize() {
    final boolean headless = GraphicsEnvironment.isHeadless();
    final Dimension d = SystemUtils.getScreenSize();
    final Dimension dw = SystemUtils.getScreenSize("Windows");
    final Dimension dl = SystemUtils.getScreenSize("Linux");
    if (headless) {
      Assertions.assertNull(d);
      Assertions.assertNull(dw);
      Assertions.assertNull(dl);
    } else {
      Assertions.assertNotNull(d);
      Assertions.assertNotNull(dw);
      Assertions.assertNotNull(dl);
    }
  }
}
