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
 * A generic resolution parameter that adds {@code name=(width)x(height)} parameter.
 */
public final class ResolutionParameter extends NoIndexParameter {

  /** The '<strong>{@code x}</strong>' character. */
  private static final char X = 'x';

  /** The width. */
  private final int width;

  /** The height. */
  private final int height;

  /**
   * Instantiates a new resolution parameter.
   *
   * @param parameter the parameter
   * @param width the width
   * @param height the height
   */
  public ResolutionParameter(Parameter parameter, int width, int height) {
    super(parameter);
    this.width = width;
    this.height = height;
  }

  /**
   * Gets the width.
   *
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the height.
   *
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return appendNameEquals(sb).append(width).append(X).append(height);
  }
}
