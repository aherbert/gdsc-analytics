/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2020 Alex Herbert
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

import java.util.EnumMap;
import java.util.Objects;

/**
 * Implements the {@link FormattedParameter} interface for {@link SessionControl}.
 *
 * @see <a href="http://goo.gl/a8d4RP#sc">Session Control</a>
 */
public final class SessionControlParameter implements FormattedParameter {

  /** Cache parameters for all the enum values. */
  private static final EnumMap<SessionControl, SessionControlParameter> CACHE =
      new EnumMap<>(SessionControl.class);

  /** The session start parameter. */
  public static final SessionControlParameter START;
  /** The session end parameter. */
  public static final SessionControlParameter END;

  static {
    START = new SessionControlParameter(SessionControl.START);
    END = new SessionControlParameter(SessionControl.END);
  }

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
    // @formatter:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.SESSION_CONTROL.getNameFormat())
        .append(Constants.EQUAL)
        .append(sessionControl.toString());
    // @formatter:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }

  /**
   * Creates the session control parameter.
   *
   * @param sessionControl the session control type
   * @return the session control parameter
   */
  public static SessionControlParameter create(SessionControl sessionControl) {
    Objects.requireNonNull(sessionControl, "Session control is null");
    return CACHE.computeIfAbsent(sessionControl, SessionControlParameter::new);
  }
}
