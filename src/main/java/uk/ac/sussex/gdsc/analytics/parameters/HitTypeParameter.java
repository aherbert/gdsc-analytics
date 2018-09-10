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
 * Implements the {@link FormattedParameter} interface for {@link HitType}.
 * 
 * @see <a href="http://goo.gl/a8d4RP#t">Hit type</a>
 */
public final class HitTypeParameter implements FormattedParameter {
  /** The pageview hit-type. */
  public static final HitTypeParameter PAGEVIEW = new HitTypeParameter(HitType.PAGEVIEW);
  /** The screenview hit-type. */
  public static final HitTypeParameter SCREENVIEW = new HitTypeParameter(HitType.SCREENVIEW);
  /** The event hit-type. */
  public static final HitTypeParameter EVENT = new HitTypeParameter(HitType.EVENT);
  /** The transaction hit-type. */
  public static final HitTypeParameter TRANSACTION = new HitTypeParameter(HitType.TRANSACTION);
  /** The item hit-type. */
  public static final HitTypeParameter ITEM = new HitTypeParameter(HitType.ITEM);
  /** The social hit-type. */
  public static final HitTypeParameter SOCIAL = new HitTypeParameter(HitType.SOCIAL);
  /** The exception hit-type. */
  public static final HitTypeParameter EXCEPTION = new HitTypeParameter(HitType.EXCEPTION);
  /** The timing hit-type. */
  public static final HitTypeParameter TIMING = new HitTypeParameter(HitType.TIMING);

  /**
   * The formatted parameter string used for the {@link FormattedParameter} interface.
   */
  private final char[] chars;

  /**
   * Creates a new instance.
   */
  private HitTypeParameter(HitType hitType) {
    //@formmater:off
    final StringBuilder sb = new StringBuilder()
        .append(ProtocolSpecification.HIT_TYPE.getNameFormat())
        .append(Constants.EQUAL)
        .append(hitType.toString());
    //@formmater:on
    chars = ParameterUtils.getChars(sb);
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    return sb.append(chars);
  }
}
