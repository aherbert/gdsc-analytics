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

import uk.ac.sussex.gdsc.analytics.parameters.Parameters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class exists to determine what happens when there is no Internet connection. This can be
 * simulated by disconnecting the host (e.g. turn WiFi off; disconnect LAN; etc.) then running the
 * test manually.
 */
@SuppressWarnings("javadoc")
public class ConnectionTest {

  // Do not use disabled as then there are test skipped warnings.
  // @org.junit.jupiter.api.Test
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

  // @org.junit.jupiter.api.Test
  public void testConnection2() throws IOException {
    isInternetAvailable();
  }

  public static boolean isInternetAvailable() throws IOException {
    return isHostAvailable("google.com") || isHostAvailable("amazon.com")
        || isHostAvailable("facebook.com") || isHostAvailable("apple.com");
  }

  private static boolean isHostAvailable(String hostName) throws IOException {
    try (Socket socket = new Socket()) {
      int port = 80;
      InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
      socket.connect(socketAddress, 3000);
      System.out.println(hostName);
      return true;
    } catch (UnknownHostException unknownHost) {
      return false;
    }
  }

  @org.junit.jupiter.api.Test
  public void testValidate() {
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String userId = "Anything";

    final GoogleAnalyticsClient ga = GoogleAnalyticsClient.createBuilder(trackingId)
        .setUserId(userId).setDebug(true).setSecure(true).build();

    // Submit requests
    String documentHostName = "www.abc.com";
    String documentPath = "/path/within/application/";
    Parameters parameters = ga.pageview(documentHostName, documentPath).build();
    DefaultHttpUrlConnectionCallback content = new DefaultHttpUrlConnectionCallback();
    DispatchStatus status = ga.getHitDispatcher().send(parameters.format(), 0, content);
    System.out.println(status);
    System.out.println(content.getResponseCode());
      System.out.println(content.getContentType());
    System.out.println(content.getBytesAsText());
  }


  /**
   * Demo of using the tracker. This code is placed in the project README.md file.
   */
  //@formatter:off
  public void demo() {
    // Create the tracker
    final String trackingId = "UA-12345-6"; // Your Google Analytics tracking ID
    final String userId = "Anything";

    final GoogleAnalyticsClient ga =
        GoogleAnalyticsClient.createBuilder(trackingId)
                             .setUserId(userId)
                             .build();

    // Submit requests
    String documentHostName = "www.abc.com";
    String documentPath = "/path/within/application/";
    ga.pageview(documentHostName, documentPath).send();

    // Shutdown
    ga.getExecutorService().shutdown();
  }
}
