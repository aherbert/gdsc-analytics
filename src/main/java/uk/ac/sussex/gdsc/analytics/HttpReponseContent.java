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

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Stores details of the response from a HTTP connection.
 */
public class HttpReponseContent {

  /** The response code. */
  private int responseCode;

  /** The response message. */
  private String responseMessage;

  /** The header fields. */
  private Map<String, List<String>> headerFields;

  /** The bytes that were read from the connection input. */
  private byte[] bytes;

  /**
   * Sets the response code.
   *
   * @param responseCode the new response code
   */
  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

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
   * Gets the response message.
   *
   * @return the response message
   * @see HttpURLConnection#getResponseMessage()
   */
  public String getResponseMessage() {
    return responseMessage;
  }

  /**
   * Sets the response message.
   *
   * @param responseMessage the new response message
   */
  public void setResponseMessage(String responseMessage) {
    this.responseMessage = responseMessage;
  }

  /**
   * Sets the header fields.
   *
   * @param headerFields the header fields
   */
  public void setHeaderFields(Map<String, List<String>> headerFields) {
    this.headerFields = headerFields;
  }

  /**
   * Gets the header fields.
   *
   * @return the header fields
   * @see URLConnection#getHeaderFields()
   */
  public Map<String, List<String>> getHeaderFields() {
    return headerFields;
  }

  /**
   * Sets the bytes.
   *
   * @param bytes the new bytes
   */
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
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
    if (bytes == null) {
      return null;
    }
    // TODO This should be better encapsulated to check the bytes are actually a charset
    // The standard Google Analytics server returns a gif image
    // The validation Google Analytics server returns UTF-8 text
    return new String(bytes, StandardCharsets.UTF_8);
  }
}
