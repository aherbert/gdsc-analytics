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

/**
 * Implements the {@link FormattedParameter} interface for {@link ProtocolVersion}.
 *
 * @see <a href="http://goo.gl/a8d4RP#sc">Session Control</a>
 */
public final class ProtocolVersionParameter implements FormattedParameter {
  /** The protocol version 1 parameter. */
  public static final ProtocolVersionParameter V1 =
      new ProtocolVersionParameter(ProtocolVersion.V1);

  /**
   * The formatted parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] chars;

  /**
   * Creates a new instance.
   *
   * @param version the version
   */
  private ProtocolVersionParameter(ProtocolVersion version) {
    // @formmater:off
    final StringBuilder sb =
        new StringBuilder().append(ProtocolSpecification.PROTOCOL_VERSION.getNameFormat())
            .append(Constants.EQUAL).append(version.getVersion());
    // @formmater:off
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }
}
