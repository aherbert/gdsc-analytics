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

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for parameters to allow storing custom dimensions and metrics. Note that custom
 * dimensions have to be created for your site before they can be used in analytics reports.
 *
 * @see <a href="https://support.google.com/analytics/answer/2709829">Create and edit custom
 *      dimensions and metrics</a>
 * @see <a href=
 *      "https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters">Measurement
 *      Protocol Parameter Reference</a>
 */
public class Parameters {

  /** The custom dimensions. */
  private List<CustomDimension> customDimensions;

  /** The custom metrics. */
  private List<CustomMetric> customMetrics;

  /**
   * Add a custom dimension
   *
   * <p>Note that custom dimensions have to be created for your site before they can be used in
   * analytics reports. Dimensions should be used for segregation of data into categories.
   *
   * <p>If the index is out of range, or the value is null then no custom dimension will be added.
   *
   * @param index The dimension index (1-20 or 1-200 for premium accounts)
   * @param value The dimension value (ignored if null)
   * @see <a href="https://support.google.com/analytics/answer/2709829">Create and edit custom
   *      dimensions and metrics</a>
   */
  public void addCustomDimension(int index, String value) {
    if (index < 1 || index > 200) {
      return;
    }
    if (value == null) {
      return;
    }
    if (customDimensions == null) {
      customDimensions = new ArrayList<>(1);
    }
    customDimensions.add(new CustomDimension(index, value));
  }

  /**
   * Gets the custom dimensions.
   *
   * @return The custom dimensions
   */
  public List<CustomDimension> getCustomDimensions() {
    return customDimensions;
  }

  /**
   * Gets the number of custom dimensions.
   *
   * @return The number of custom dimensions
   */
  public int getNumberOfCustomDimensions() {
    return (customDimensions == null) ? 0 : customDimensions.size();
  }

  /**
   * Checks for custom dimensions.
   *
   * @return True if there are custom dimensions
   */
  public boolean hasCustomDimensions() {
    return customDimensions != null;
  }

  /**
   * Add a custom metric
   *
   * <p>Note that custom metrics have to be created for your site before they can be used in
   * analytics reports. Metrics should be used for numbers that you want to accumulate.
   *
   * <p>If the index is out of range then no custom dimension will be added.
   *
   * @param index The dimension index (1-20 or 1-200 for premium accounts)
   * @param value The metric value
   * @see <a href="https://support.google.com/analytics/answer/2709829">Create and edit custom
   *      dimensions and metrics</a>
   */
  public void addCustomMetric(int index, int value) {
    if (index < 1 || index > 200) {
      return;
    }
    if (customMetrics == null) {
      customMetrics = new ArrayList<>(1);
    }
    customMetrics.add(new CustomMetric(index, value));
  }

  /**
   * Gets the custom metrics.
   *
   * @return The custom metrics
   */
  public List<CustomMetric> getCustomMetrics() {
    return customMetrics;
  }

  /**
   * Gets the number of custom metrics.
   *
   * @return The number of customer metrics
   */
  public int getNumberOfCustomMetrics() {
    return (customMetrics == null) ? 0 : customMetrics.size();
  }

  /**
   * Checks for custom metrics.
   *
   * @return True if there are custom metrics
   */
  public boolean hasCustomMetrics() {
    return customMetrics != null;
  }
}
