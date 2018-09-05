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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility functions for URLs.
 */
public final class UrlUtils {

  /**
   * No public construction.
   */
  private UrlUtils() {
    // Do nothing
  }

  /**
   * Construct a proxy from an address.
   * 
   * <p>If no proxy can be set then this will return null.
   *
   * @param proxyAddress "hostname:port" of the proxy to use; may also be given as URL
   *        ("http://hostname:port/").
   * @return the proxy (or null)
   */
  public static Proxy getProxy(String proxyAddress) {
    if (proxyAddress != null) {
      // Split into "hostname:port"
      final Matcher m = Pattern.compile("^(https?://|)([^ :]+):([0-9]+)").matcher(proxyAddress);
      if (m.find()) {
        final String hostname = m.group(2);
        final int port = Integer.parseInt(m.group(3));

        final SocketAddress sa = new InetSocketAddress(hostname, port);
        return new Proxy(Type.HTTP, sa);
      }
    }
    return null;
  }
}
