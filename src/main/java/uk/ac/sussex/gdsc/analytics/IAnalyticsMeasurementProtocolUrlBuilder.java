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

/**
 * URL builder for the tracking requests.
 */
public interface IAnalyticsMeasurementProtocolUrlBuilder {
  /**
   * Gets the version for this builder.
   *
   * @return The version
   */
  String getVersion();

  /**
   * Build the parameters URL request from the data. The parameters are suitable for use in the HTTP
   * POST method.
   *
   * @param clientParameters The client parameter data
   * @param requestParameters The request parameter data
   * @param timestamp The timestamp when the hit was reported (in milliseconds)
   * @return The parameters URL
   */
  String buildUrl(ClientParameters clientParameters, RequestParameters requestParameters,
      long timestamp);

  /**
   * Build the parameters URL request from the data. The parameters are suitable for use in the HTTP
   * GET method.
   *
   * @param clientParameters The client parameter data
   * @param requestParameters The request parameter data
   * @param timestamp The timestamp when the hit was reported (in milliseconds)
   * @return The parameters URL
   */
  String buildGetUrl(ClientParameters clientParameters, RequestParameters requestParameters,
      long timestamp);
}
