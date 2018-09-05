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

import java.util.Locale;

/**
 * Stores valid values for session control.
 * 
 * @see <a href="http://goo.gl/a8d4RP#sc">Session Control</a>
 */
public enum SessionControlParameter implements FormattedParameter {
  /** Forces a new session to start with this hit. */
  START,
  /** Forces the current session to end with this hit. */
  END;

  /** The name. */
  private final String name;

  /**
   * The parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] parameter;

  /**
   * Instantiates a new hit type.
   */
  private SessionControlParameter() {
    this.name = super.toString().toLowerCase(Locale.getDefault());
    parameter = ("&sc=" + name).toCharArray();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }

  @Override
  public void appendTo(StringBuilder sb) {
    sb.append(parameter);
  }
}
