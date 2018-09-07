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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Stores details of the response from a HTTP connection that are relevant for Google Analytics.
 */
public class DefaultHttpUrlConnectionCallback implements HttpUrlConnectionCallback {

  /** The buffer size when reading the input stream. */
  private static final int BUFFER_SIZE = 1024;

  /** The response code. */
  private int responseCode;

  /** The content type. */
  private String contentType;

  /** The bytes that were read from the connection input. */
  private byte[] bytes;

  /**
   * Gets the response code.
   *
   * @return the response code
   * @see HttpURLConnection#getResponseCode()
   */
  public int getResponseCode() {
    return responseCode;
  }

  /**
   * Gets the content type.
   *
   * @return the content type
   * @see HttpURLConnection#getContentType()
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Gets the bytes that were read from the connection input.
   *
   * @return the bytes
   * @see URLConnection#getInputStream()
   */
  public byte[] getBytes() {
    return bytes;
  }

  /**
   * Gets the bytes as text assuming UTF-8 encoding (the default for the Google Analytics validation
   * server).
   *
   * @return the bytes as text (or null if no bytes)
   */
  public String getBytesAsText() {
    if (bytes == null || contentType == null) {
      return null;
    }
    // The standard Google Analytics server returns a gif image.
    // The validation Google Analytics server returns utf-8 text.
    return (contentType.endsWith("charset=utf-8"))
        // This is expected
        ? new String(bytes, StandardCharsets.UTF_8)
        : null;
  }

  @Override
  public void process(HttpURLConnection connection) throws IOException {
    reset();
    responseCode = connection.getResponseCode();
    contentType = connection.getContentType();

    // Read byte data assuming UTF-8.
    try (final InputStream inputStream = connection.getInputStream()) {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);
      int readCount;
      final byte[] data = new byte[BUFFER_SIZE];
      while ((readCount = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, readCount);
      }
      buffer.flush();
      bytes = buffer.toByteArray();
    }
  }

  /**
   * Reset the collected properties to the defaults for the Java type.
   */
  public void reset() {
    responseCode = 0;
    contentType = null;
    bytes = null;
  }
}
