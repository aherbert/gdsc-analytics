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

package uk.ac.sussex.gdsc.analytics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

/**
 * This class exists to determine what happens when there is no Internet connection. This can be
 * simulated by disconnecting the host (e.g. turn WiFi off; disconnect LAN; etc.) then running the
 * test manually.
 */
@SuppressWarnings("javadoc")
public class ConnectionTest {

  // Do not use disabled as then there are test skipped warnings.
  @Test
  public void testConnection() {
    final Logger logger = Logger.getLogger(ConnectionTest.class.getName());
    HttpURLConnection connection = null;
    try {
      final URL url = new URL("http://www.google.com");
      connection = (HttpURLConnection) url.openConnection();
      logger.info("Timeout = " + connection.getConnectTimeout());
      connection.connect();
      final int responseCode = connection.getResponseCode();
      logger.info("Response = " + responseCode);
    } catch (final UnknownHostException ex) {
      logger.log(Level.WARNING, "Unknown host: " + ex.getMessage(), ex);
    } catch (final IOException ex) {
      logger.log(Level.SEVERE, "Error making tracking request: " + ex.getMessage(), ex);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  @Test
  public static boolean testInternetAvailable() throws IOException {
    return isHostAvailable("google.com") || isHostAvailable("amazon.com")
        || isHostAvailable("facebook.com") || isHostAvailable("apple.com");
  }

  private static boolean isHostAvailable(String hostName) throws IOException {
    try (Socket socket = new Socket()) {
      final int port = 80;
      final InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
      socket.connect(socketAddress, 3000);
      System.out.println(hostName);
      return true;
    } catch (final UnknownHostException unknownHost) {
      return false;
    }
  }
}
