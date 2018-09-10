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
    if (GraphicsEnvironment.isHeadless()) {
      return null;
    }
    Dimension result;
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
