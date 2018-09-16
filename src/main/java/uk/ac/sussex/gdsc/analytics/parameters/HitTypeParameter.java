/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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

import java.util.Objects;

/**
 * Implements the {@link FormattedParameter} interface for {@link HitType}.
 *
 * @see <a href="http://goo.gl/a8d4RP#t">Hit type</a>
 */
public final class HitTypeParameter implements FormattedParameter {
  /** The pageview hit-type. */
  public static final HitTypeParameter PAGEVIEW;
  /** The screenview hit-type. */
  public static final HitTypeParameter SCREENVIEW;
  /** The event hit-type. */
  public static final HitTypeParameter EVENT;
  /** The transaction hit-type. */
  public static final HitTypeParameter TRANSACTION;
  /** The item hit-type. */
  public static final HitTypeParameter ITEM;
  /** The social hit-type. */
  public static final HitTypeParameter SOCIAL;
  /** The exception hit-type. */
  public static final HitTypeParameter EXCEPTION;
  /** The timing hit-type. */
  public static final HitTypeParameter TIMING;

  static {
    PAGEVIEW = new HitTypeParameter(HitType.PAGEVIEW);
    SCREENVIEW = new HitTypeParameter(HitType.SCREENVIEW);
    EVENT = new HitTypeParameter(HitType.EVENT);
    TRANSACTION = new HitTypeParameter(HitType.TRANSACTION);
    ITEM = new HitTypeParameter(HitType.ITEM);
    SOCIAL = new HitTypeParameter(HitType.SOCIAL);
    EXCEPTION = new HitTypeParameter(HitType.EXCEPTION);
    TIMING = new HitTypeParameter(HitType.TIMING);
  }

  /**
   * The formatted parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] chars;

  /**
   * Creates a new instance.
   */
  private HitTypeParameter(HitType hitType) {
    // @formatter:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.HIT_TYPE.getNameFormat())
        .append(Constants.EQUAL)
        .append(hitType.toString());
    // @formatter:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }

  /**
   * Creates the hit type parameter
   *
   * @param hitType the hit type
   * @return the hit type parameter
   */
  public static HitTypeParameter create(HitType hitType) {
    Objects.requireNonNull(hitType, "Hit type is null");
    switch (hitType) {
      case EVENT:
        return EVENT;
      case EXCEPTION:
        return EXCEPTION;
      case ITEM:
        return ITEM;
      case PAGEVIEW:
        return PAGEVIEW;
      case SCREENVIEW:
        return SCREENVIEW;
      case SOCIAL:
        return SOCIAL;
      case TIMING:
        return TIMING;
      case TRANSACTION:
      default:
        return TRANSACTION;
    }
  }
}
