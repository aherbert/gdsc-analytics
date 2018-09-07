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
 * Implements the {@link FormattedParameter} interface for {@link SessionControl}.
 * 
 * @see <a href="http://goo.gl/a8d4RP#sc">Session Control</a>
 */
public final class SessionControlParameter implements FormattedParameter {
  /** The session start parameter. */
  public static final SessionControlParameter START =
      new SessionControlParameter(SessionControl.START);
  /** The session end parameter. */
  public static final SessionControlParameter END = new SessionControlParameter(SessionControl.END);

  /**
   * The formatted parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] chars;

  /**
   * Creates a new instance.
   *
   * @param sessionControl the session control
   */
  private SessionControlParameter(SessionControl sessionControl) {
    //@formmater:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.SESSION_CONTROL.getNameFormatRef())
        .append(Constants.EQUAL)
        .append(sessionControl.toString());
    //@formmater:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }
}
