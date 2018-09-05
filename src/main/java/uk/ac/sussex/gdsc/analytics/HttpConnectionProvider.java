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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Interface to allow a custom connection to be opened for a URL.
 *
 * <p>This exists for testing purposes.
 */
interface HttpConnectionProvider {

  /**
   * Open a connection.
   *
   * @param url the url (assumed to be a HTTP/HTTPS protocol)
   * @param proxy the proxy (may be null)
   * @return the http URL connection
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws ClassCastException If the connection cannot be cast to a HttpURLConnection
   * @see URL#openConnection()
   * @see URL#openConnection(Proxy)
   */
  default HttpURLConnection openConnection(URL url, Proxy proxy)
      throws IOException, ClassCastException {
    return (HttpURLConnection) ((proxy == null) ? url.openConnection() : url.openConnection(proxy));
  }
}
