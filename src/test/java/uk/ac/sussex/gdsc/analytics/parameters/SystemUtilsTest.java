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
    boolean headless = GraphicsEnvironment.isHeadless();
    Dimension d = SystemUtils.getScreenSize();
    Dimension dw = SystemUtils.getScreenSize("Windows");
    Dimension dl = SystemUtils.getScreenSize("Linux");
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
