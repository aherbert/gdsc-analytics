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

package uk.ac.sussex.gdsc.analytics.parameters;

import java.util.UUID;

/**
 * Adds the Client ID (cid) parameter.
 * 
 * <p>This field is required if User ID (uid) is not specified in the request.
 * 
 * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
 */
public class ClientIdParameter extends TextParameter {

  /**
   * Instantiates a new instance.
   * 
   * <p>The value should be a random UUID (version 4) as described in <a
   * href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>.
   *
   * @param clientId the client id
   * @throws IllegalArgumentException If not a valid UUID
   * @see UUID#fromString(String)
   */
  public ClientIdParameter(String clientId) {
    super(Parameter.CLIENT_ID, clientId);
    // Try and parse the client Id to a UUID to test it is valid
    UUID.fromString(clientId);
  }

  /**
   * Instantiates a new instance using the string representation of the UUID.
   *
   * @param clientId the client id
   * @see UUID#toString()
   */
  public ClientIdParameter(UUID clientId) {
    super(Parameter.CLIENT_ID, clientId.toString());
  }
}
