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

/**
 * Provide a framework to send raw user interaction data directly to Google Analytics servers via
 * the <a href= "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google
 * Analytics Measurement Protocol</a>.
 *
 * <p>Since the code will only be used within a Java application the referral, search referral and
 * campaign functionality has been removed to simplify the analytics and allow caching most of the
 * constructed analytics URL.
 *
 * @see <a href= "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google
 *      Analytics Measurement Protocol</a>
 * @since 1.0.0
 */
package uk.ac.sussex.gdsc.analytics;
