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

import java.util.UUID;

/**
 * Adds the Client ID (cid) parameter.
 *
 * <p>This field is required if User ID (uid) is not specified in the request.
 *
 * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
 */
public class ClientIdParameter extends NoIndexTextParameter {

  /**
   * Creates a new instance.
   *
   * <p>The value should be a random UUID (version 4) as described in <a
   * href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>.
   *
   * @param clientId the client id
   * @throws IllegalArgumentException If not a valid UUID
   * @see UUID#fromString(String)
   */
  public ClientIdParameter(String clientId) {
    super(ProtocolSpecification.CLIENT_ID, clientId);
    // Try and parse the client Id to a UUID to test it is valid
    UUID.fromString(clientId);
  }

  /**
   * Creates a new instance.using the string representation of the UUID.
   *
   * @param clientId the client id
   * @see UUID#toString()
   */
  public ClientIdParameter(UUID clientId) {
    super(ProtocolSpecification.CLIENT_ID, clientId.toString());
  }
}
