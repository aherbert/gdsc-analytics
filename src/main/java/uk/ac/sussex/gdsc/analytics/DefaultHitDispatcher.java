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

import uk.ac.sussex.gdsc.analytics.parameters.QueueTimeParameter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends hit requests to the Google Analytics server.
 */
public class DefaultHitDispatcher implements HitDispatcher {

  /** The logger. */
  private static final Logger logger = Logger.getLogger(DefaultHitDispatcher.class.getName());

  /**
   * The shared last IO exception that occurred when dispatching a request. If this is not null then
   * the tracker is disabled as it is assumed that all subsequent tracking requests will fail.
   * 
   * <p>This is shared among all instances returned by {@link #getDefault(boolean, boolean)}.
   */
  private static AtomicReference<IOException> sharedIoException = new AtomicReference<>();

  /** The url used for tracking requests. */
  private final URL url;

  /** The proxy used for tracking requests. */
  private Proxy proxy;

  /** The connection provider. */
  private HttpConnectionProvider connectionProvider;

  /** The disabled flag. */
  private volatile boolean disabled;

  /**
   * The last IO exception that occurred when dispatching a request. If this is not null then the
   * tracker is disabled as it is assumed that all subsequent tracking requests will fail.
   */
  private final AtomicReference<IOException> lastIoException;

  /**
   * Create a new instance.
   *
   * @param url the url
   */
  public DefaultHitDispatcher(URL url) {
    this(url, null);
  }

  /**
   * Create a new instance.
   *
   * @param url the url
   * @param connectionProvider the connection provider (may be null)
   */
  public DefaultHitDispatcher(URL url, HttpConnectionProvider connectionProvider) {
    this(url, connectionProvider, null);
  }

  /**
   * Create a new instance.
   *
   * @param url the url
   * @param connectionProvider the connection provider (may be null)
   * @param proxy the proxy (may be null)
   */
  public DefaultHitDispatcher(URL url, HttpConnectionProvider connectionProvider, Proxy proxy) {
    // New instances will have their own IO Exception
    this(url, connectionProvider, proxy, new AtomicReference<>());
  }

  /**
   * Create a new instance.
   *
   * @param url the url (required)
   * @param connectionProvider the connection provider (may be null)
   * @param proxy the proxy (may be null)
   * @param lastIoException the last io exception (required)
   */
  private DefaultHitDispatcher(URL url, HttpConnectionProvider connectionProvider, Proxy proxy,
      AtomicReference<IOException> lastIoException) {
    this.url = Objects.requireNonNull(url, "URL is null");
    if (connectionProvider == null) {
      this.connectionProvider = new HttpConnectionProvider() {
        // Use the default implementation in the interface
      };
    } else {
      this.connectionProvider = connectionProvider;
    }
    // Does not matter if this is null
    this.proxy = proxy;
    // This should not be null but check anyway
    this.lastIoException = Objects.requireNonNull(lastIoException, "Invalid internal state");
  }

  /**
   * Gets a dispatcher configured to use the default connection to Google Analytics.
   * 
   * <p>Any errors that occur when dispatching requests will be shared among all instances created
   * using this method. This ensures that if an error occurs connecting to the default URL then the
   * system will be disabled.
   *
   * @param secure the secure
   * @param debug the debug
   * @return the default
   * @see GoogleAnalyticsUtils#getGoogleAnalyticsUrl(boolean, boolean)
   */
  public static DefaultHitDispatcher getDefault(boolean secure, boolean debug) {
    // This URL should be effectively final as it is set using System properties
    final URL url = GoogleAnalyticsUtils.getGoogleAnalyticsUrl(secure, debug);
    return new DefaultHitDispatcher(url, null, null, sharedIoException);
  }

  /**
   * {@inheritDoc}
   * 
   * <p>The callback is invoked after the hit has been sent and
   * {@link HttpURLConnection#getResponseCode()} has returned successfully.
   * 
   * @see HitDispatcher#send(CharSequence, long, HttpUrlConnectionCallback)
   */
  @Override
  public DispatchStatus send(CharSequence hit, long timestamp, HttpUrlConnectionCallback callback) {
    // Do nothing if disabled
    if (isDisabled()) {
      return DispatchStatus.DISABLED;
    }
    HttpURLConnection connection = null;
    try {
      connection = connectionProvider.openConnection(url, proxy);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded; charset=utf-8");
      // Build the request
      CharSequence request;
      // Add the queue time offset
      if (timestamp != 0) {
        StringBuilder sb;
        if (hit instanceof StringBuilder) {
          sb = (StringBuilder) hit;
        } else {
          sb = new StringBuilder(hit);
        }
        QueueTimeParameter.appendTo(sb, timestamp);
        request = sb;
      } else {
        request = hit;
      }
      // Send the request
      final byte[] out = request.toString().getBytes(StandardCharsets.UTF_8);
      final int length = out.length;
      connection.setFixedLengthStreamingMode(length);
      connection.connect();
      try (OutputStream os = connection.getOutputStream()) {
        os.write(out);
      }
      final int responseCode = connection.getResponseCode();
      if (callback != null) {
        callback.process(connection);
      }

      //////////////////////////////////////
      // Note: Valid on 31-Aug-2018
      // https://developers.google.com/analytics/devguides/collection/protocol/v1/validating-hits
      // "The Google Analytics Measurement Protocol does not return HTTP error codes".
      // So the response code will ALWAYS be HTTP_OK.
      // However since the connection may be to something else via the connection provider,
      // or Google change this response in the future we process the result anyway.
      //////////////////////////////////////

      if (responseCode == HttpURLConnection.HTTP_OK) {
        if (logger.isLoggable(Level.FINE)) {
          logger.log(Level.FINE, () -> String.format("Tracking success for url '%s'", request));
        }
        // This is a success. All other returns are false.
        return DispatchStatus.COMPLETE;
      }
      logger.log(Level.WARNING, () -> String
          .format("Error requesting url '%s', received response code %d", request, responseCode));
    } catch (final UnknownHostException ex) {
      setLastIoException(ex);
      // Occurs when disconnected from the Internet so this is not severe
      logger.log(Level.WARNING, () -> String.format("Unknown host: %s", ex.getMessage()));
    } catch (final IOException ex) {
      setLastIoException(ex);
      // Log all others at a severe level
      logger.log(Level.SEVERE, () -> String.format("Error making tracking request: %s : %s",
          ex.getClass().getSimpleName(), ex.getMessage()));
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    // Get here only on error
    return DispatchStatus.ERROR;
  }

  /**
   * Sets the last IO exception.
   *
   * <p>This will disable all tracking requests.
   *
   * @param ex the last IO exception
   */
  private void setLastIoException(IOException ex) {
    lastIoException.set(ex);
  }

  /**
   * Gets the last IO exception that occurred from a dispatch request.
   *
   * <p>If this is not {@code null} then all tracking is disabled as it is assumed that all
   * subsequent tracking requests will fail.
   *
   * @return the last IO exception
   */
  @Override
  public IOException getLastIoException() {
    return lastIoException.get();
  }

  @Override
  public boolean isDisabled() {
    return disabled || lastIoException.get() != null;
  }

  @Override
  public boolean start() {
    setLastIoException(null);
    disabled = false;
    return isDisabled();
  }

  @Override
  public boolean stop() {
    disabled = true;
    return true;
  }
}
