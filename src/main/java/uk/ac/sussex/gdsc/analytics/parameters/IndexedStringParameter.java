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

/**
 * Stores a named indexed {@code String} parameter, e.g. {@code key+keyId=value} where
 * {@code key+keyId} is the equivalent of the string result of adding the key and the key Id.
 * 
 * @see <a
 *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cd_">Custom
 *      Dimension</a>
 * @see <a
 *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cg_">Content
 *      Group</a>
 */
public class IndexedStringParameter extends StringParameter {
  /** The index. */
  private final int index;

  /**
   * Instantiates a new instance.
   *
   * @param name the name
   * @param index the index
   * @param value the value
   * @throws IllegalArgumentException If the index is not strictly positive
   */
  public IndexedStringParameter(String name, int index, String value)
      throws IllegalArgumentException {
    super(name, value);
    this.index = ParameterUtils.requireStrictlyPositive(index, "index");
  }

  /**
   * Gets the index.
   *
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  @Override
  public void appendTo(StringBuilder sb) {
    FormattedParameterHelper.append(sb, getName(), index, getValue());
  }
}
