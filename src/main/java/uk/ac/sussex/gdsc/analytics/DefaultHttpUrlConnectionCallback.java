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

  /** Used to reset the content type. */
  private static final String UNKNOWN_CONTENT_TYPE = null;

  /** Used to reset the bytes. */
  private static final byte[] NO_BYTES = null;

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
    return (bytes == null) ? null : bytes.clone();
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
    try (InputStream inputStream = connection.getInputStream()) {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);
      final byte[] data = new byte[BUFFER_SIZE];
      int readCount = inputStream.read(data, 0, data.length);
      while (readCount != -1) {
        buffer.write(data, 0, readCount);
        readCount = inputStream.read(data, 0, data.length);
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
    contentType = UNKNOWN_CONTENT_TYPE;
    bytes = NO_BYTES;
  }
}
