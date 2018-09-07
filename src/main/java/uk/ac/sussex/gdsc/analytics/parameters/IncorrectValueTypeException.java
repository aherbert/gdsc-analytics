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
 * Thrown when the expected value type did not match the observed value type.
 */
public class IncorrectValueTypeException extends RuntimeException {

  /**
   * The serial version ID.
   */
  private static final long serialVersionUID = 5804629685439288868L;

  /** The index count. */
  private final ValueType expected;

  /** The required index count. */
  private final ValueType observed;


  /** The detail message. */
  private final String detailMessage;

  /**
   * Instantiates a new incorrect index value type exception.
   *
   * @param expected the expected value
   * @param observed the observed value
   * @param detailMessage the detail message
   */
  public IncorrectValueTypeException(ValueType expected, ValueType observed, String detailMessage) {
    this.expected = expected;
    this.observed = observed;
    this.detailMessage = detailMessage;
  }

  /**
   * Gets the expected.
   *
   * @return the expected
   */
  public ValueType getExpected() {
    return expected;
  }

  /**
   * Gets the observed.
   *
   * @return the observed
   */
  public ValueType getObserved() {
    return observed;
  }

  @Override
  public String getMessage() {
    String msg =
        String.format("Incorrect value type: expected <%s>, observed <%s>", expected, observed);
    if (ParameterUtils.isNotEmpty(detailMessage)) {
      msg += ": " + detailMessage;
    }
    return msg;
  }
}
