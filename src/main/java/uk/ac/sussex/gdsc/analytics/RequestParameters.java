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

import java.util.Objects;

/**
 * Google Analytics request data.
 *
 * @see <a href=
 *      "https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters">Measurement
 *      Protocol Parameter Reference</a>
 */
public class RequestParameters extends Parameters {

  /** The hit type. */
  private final HitType hitType;

  /** The document path. */
  private String documentPath = null;

  /** The document title. */
  private String documentTitle = null;

  /** The category. */
  private String category;

  /** The action. */
  private String action;

  /** The label. */
  private String label;
  /**
   * The value. This is an object to allow null to represent unset.
   */
  private Integer value;

  /**
   * Instantiates a new request parameters.
   *
   * @param hitType the hit type
   */
  public RequestParameters(HitType hitType) {
    Objects.requireNonNull(hitType, "Hit type is null");
    this.hitType = hitType;
  }

  /**
   * Gets the hit type.
   *
   * @return The hit type
   */
  public String getHitType() {
    return hitType.toString();
  }

  /**
   * Gets the hit type enum.
   *
   * @return The hit type
   */
  public HitType getHitTypeEnum() {
    return hitType;
  }

  /**
   * Gets the document path.
   *
   * @return the document path
   */
  public String getDocumentPath() {
    return documentPath;
  }

  /**
   * Sets the document path.
   *
   * @param documentPath the document path to set
   */
  public void setDocumentPath(String documentPath) {
    this.documentPath = documentPath;
  }

  /**
   * Gets the document title.
   *
   * @return the document title
   */
  public String getDocumentTitle() {
    return documentTitle;
  }

  /**
   * Sets the document title.
   *
   * @param documentTitle the document title to set
   */
  public void setDocumentTitle(String documentTitle) {
    this.documentTitle = documentTitle;
  }

  /**
   * Gets the category.
   *
   * @return the event category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the category.
   *
   * @param category the event category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets the action.
   *
   * @return the event action
   */
  public String getAction() {
    return action;
  }

  /**
   * Sets the action.
   *
   * @param action the event action to set
   */
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * Gets the label.
   *
   * @return the event label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   *
   * @param label the event label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the value.
   *
   * @return the event value
   */
  public Integer getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the event value to set
   */
  public void setValue(Integer value) {
    this.value = value;
  }

  /**
   * Add a hit level custom dimension.
   *
   * @param index the index
   * @param value the value
   * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomDimension(int, java.lang.String)
   */
  @Override
  public void addCustomDimension(int index, String value) {
    super.addCustomDimension(index, value);
  }

  /**
   * Add a hit level custom metric.
   *
   * @param index the index
   * @param value the value
   * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomMetric(int, int)
   */
  @Override
  public void addCustomMetric(int index, int value) {
    super.addCustomMetric(index, value);
  }
}
