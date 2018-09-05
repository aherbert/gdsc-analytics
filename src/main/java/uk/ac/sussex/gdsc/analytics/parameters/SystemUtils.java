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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * Utility methods to obtain parameter information from the system.
 */
public final class SystemUtils {

  /**
   * No public construction.
   */
  private SystemUtils() {
    // Do nothing
  }

  /**
   * Get the screen size.
   *
   * @return The dimension of the primary screen (or null if headless)
   */
  public static Dimension getScreenSize() {
    return getScreenSize(System.getProperty("os.name"));
  }

  /**
   * Get the screen size.
   *
   * <p>Adapted from ij.IJ.getScreenSize() in the ImageJ code.
   *
   * @param osName The os.name system property
   * @return The dimension of the primary screen (or null if headless)
   * @see <a href="http://imagej.nih.gov/ij/">Image J</a>
   */
  static Dimension getScreenSize(String osName) {
    Dimension result;
    if (GraphicsEnvironment.isHeadless()) {
      result = null;
    } else {
      // GraphicsEnvironment.getConfigurations is *very* slow on Windows
      if (isWindows(osName)) {
        result = Toolkit.getDefaultToolkit().getScreenSize();
      } else {
        // Can't use Toolkit.getScreenSize() on Linux because it returns
        // size of all displays rather than just the primary display.
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        final GraphicsConfiguration[] gc = gd[0].getConfigurations();
        final Rectangle bounds = gc[0].getBounds();
        if ((bounds.x == 0 && bounds.y == 0) || (isLinux(osName) && gc.length > 1)) {
          result = new Dimension(bounds.width, bounds.height);
        } else {
          result = Toolkit.getDefaultToolkit().getScreenSize();
        }
      }
    }
    return result;
  }

  /**
   * Checks if is windows.
   *
   * @param osName the os name
   * @return true, if is windows
   */
  static boolean isWindows(String osName) {
    return osName.startsWith("Windows");
  }

  /**
   * Checks if is linux.
   *
   * @param osName the os name
   * @return true, if is linux
   */
  static boolean isLinux(String osName) {
    return osName.startsWith("Linux");
  }
}
