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

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Populates the client with information from the system.
 */
public class ClientParametersManager {

  /**
   * No public construction.
   */
  private ClientParametersManager() {
    // Do nothing
  }

  /**
   * Populates the client parameters with information from the system.
   *
   * @param data The data
   */
  public static final void populate(ClientParameters data) {
    String region = System.getProperty("user.region");
    if (region == null) {
      region = System.getProperty("user.country");
    }
    data.setUserLanguage((System.getProperty("user.language") + "-" + region).toLowerCase());

    // Do not collect the hostname be default
    data.setHostName("localhost");

    final String osName = System.getProperty("os.name");

    final Dimension d = getScreenSize(osName);
    data.setScreenResolution(d.width + "x" + d.height);

    // The browser and operating system are taken from the User-Agent property.

    // data.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36
    // (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"); // To simulate Chrome

    // The Java URLConnection User-Agent property will default to 'Java/1.6.0.19'
    // where the
    // last part is the JRE version. Add the operating system to this, e.g.
    // Java/1.6.0.19 (Windows NT 6.1)

    final StringBuilder sb = new StringBuilder();
    sb.append("Java/").append(System.getProperty("java.version"));
    sb.append(" (").append(getPlatform(osName)).append(")");
    data.setUserAgent(sb.toString());

    // Note: Adding the OS does not currently work within Google Analytics.
    //
    // https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ua
    // "The User Agent of the browser. Note that Google has libraries to identify
    // real user agents.
    // Hand crafting your own agent could break at any time."

    // A better option is to pass in custom dimension so this data can be used in
    // reports.
  }

  /**
   * Populates the client parameters with the system hostname.
   *
   * <p>The call will timeout after 2 seconds (e.g. if the DNS is not working) and the hostname will
   * be set to localhost.
   *
   * @param data The data
   */
  public static final void populateHostname(ClientParameters data) {
    String hostName = "localhost";

    // This can wait for a long time (e.g. if the DNS is not working).
    // Write so that it can timeout without causing a delay to the calling program.
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<String> future = executor.submit(new Callable<String>() {
      @Override
      public String call() {
        String hostName = "localhost";
        try {
          final InetAddress iAddress = InetAddress.getLocalHost();
          // This performs a lookup of the name service as well
          // e.g. host.domain.com
          hostName = iAddress.getCanonicalHostName();

          // This only retrieves the bare hostname
          // e.g. host
          // hostName = iAddress.getHostName();

          // This retrieves the IP address as a string
          // e.g. 192.168.0.1
          // hostName = iAddress.getHostAddress();
        } catch (final UnknownHostException ex) {
          // ignore this
        }
        return hostName;
      }
    });
    try {
      hostName = future.get(2, TimeUnit.SECONDS); // timeout is in 2 seconds
    } catch (final TimeoutException ex) {
      final Logger logger = Logger.getLogger(ClientParametersManager.class.getName());
      logger.fine("Timeout when resolving hostname");
    } catch (final InterruptedException ex) {
      // ignore this
    } catch (final ExecutionException ex) {
      // ignore this
    }
    executor.shutdownNow();

    data.setHostName(hostName);
  }

  /**
   * Get the platform for the User-Agent string.
   *
   * @param osName the os name
   * @return The platform
   */
  static String getPlatform(String osName) {
    // Note that on Windows the os.version property does not directly translate into
    // the user agent platform token:
    // https://msdn.microsoft.com/en-gb/library/ms537503(v=vs.85).aspx
    // https://en.wikipedia.org/wiki/Windows_NT#Releases
    final String lc_os_name = osName.toLowerCase();
    if (lc_os_name.contains("windows")) {
      //@formatter:off
      if (lc_os_name.contains("server 2016"))     { return "Windows NT 10.0"; }
      if (lc_os_name.contains("server 2012 r2"))  { return "Windows NT 6.3"; }
      if (lc_os_name.contains("server 2012"))     { return "Windows NT 6.2"; }
      if (lc_os_name.contains("server 2011"))     { return "Windows NT 6.1"; }
      if (lc_os_name.contains("server 2008 r2"))  { return "Windows NT 6.1"; }
      if (lc_os_name.contains("server 2008"))     { return "Windows NT 6.0"; }
      if (lc_os_name.contains("server 2003"))     { return "Windows NT 5.2"; }
      if (lc_os_name.contains("vista"))           { return "Windows NT 6.0"; }
      if (lc_os_name.contains("xp x64"))          { return "Windows NT 5.2"; }
      if (lc_os_name.contains("xp"))              { return "Windows NT 5.1"; }
      if (lc_os_name.contains("2000, service"))   { return "Windows NT 5.01"; }
      if (lc_os_name.contains("2000"))            { return "Windows NT 5.0"; }
      if (lc_os_name.contains("nt 4"))            { return "Windows NT 4.0"; }
      if (lc_os_name.contains("mw"))              { return "Windows 98; Win 9x 4.90"; }
      if (lc_os_name.contains("98"))              { return "Windows 98"; }
      if (lc_os_name.contains("95"))              { return "Windows 95"; }
      if (lc_os_name.contains("ce"))              { return "Windows CE"; }
      if (lc_os_name.contains("10"))              { return "Windows NT 10.0"; }
      if (lc_os_name.contains("8.1"))             { return "Windows NT 6.3"; }
      if (lc_os_name.contains("8"))               { return "Windows NT 6.2"; }
      if (lc_os_name.contains("7"))               { return "Windows NT 6.1"; }
      return "Windows NT 6.1"; // Default to Windows 7
      //@formatter:on
    }

    // Mac - Note sure what to put here.
    // E.g. Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14
    // (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A
    if (lc_os_name.startsWith("mac")) {
      // Just pick a recent valid platform from a valid Mac User-Agent string
      return "Macintosh; Intel Mac OS X 10_9_3";
    }

    // Linux variants will just return 'Linux'.
    // This is apparently detected by Google Analytics so we leave this as is.

    // Other - Just leave it

    final String os_version = System.getProperty("os.version");
    return osName + " " + os_version;
  }

  /**
   * Get the screen size.
   *
   * <p>Adapted from ij.IJ.getScreenSize() in the ImageJ code.
   *
   * @param osName The os.name system property
   * @return The dimension of the primary screen
   * @see <a href="http://imagej.nih.gov/ij/">Image J</a>
   */
  public static Dimension getScreenSize(String osName) {
    if (GraphicsEnvironment.isHeadless()) {
      return new Dimension(0, 0);
    }
    if (isWindows(osName)) { // GraphicsEnvironment.getConfigurations is *very* slow on Windows
      return Toolkit.getDefaultToolkit().getScreenSize();
    }
    // Can't use Toolkit.getScreenSize() on Linux because it returns
    // size of all displays rather than just the primary display.
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final GraphicsDevice[] gd = ge.getScreenDevices();
    final GraphicsConfiguration[] gc = gd[0].getConfigurations();
    final Rectangle bounds = gc[0].getBounds();
    if ((bounds.x == 0 && bounds.y == 0) || (isLinux(osName) && gc.length > 1)) {
      return new Dimension(bounds.width, bounds.height);
    }
    return Toolkit.getDefaultToolkit().getScreenSize();
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
