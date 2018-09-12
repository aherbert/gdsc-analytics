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
 * A generic text parameter for a {@link ParameterSpecification} with 2 indexes.
 */
public class TwoIndexTextParameter extends TwoIndexParameter {

  /** The required value type. */
  private static final ValueType REQUIRED = ValueType.TEXT;

  /** The value. */
  private final String value;

  /** The encoded value. */
  private String encodedValue;

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public TwoIndexTextParameter(ParameterSpecification specification, int index1, int index2,
      String value) {
    super(specification, index1, index2);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = Objects.requireNonNull(value, "Value");
  }

  /**
   * Creates a new instance.
   *
   * @param specification the specification
   * @param index1 the first index
   * @param index2 the second index
   * @param value the value
   * @throws IncorrectCountException If the parameter index count is not zero
   * @throws IncorrectValueTypeException If the parameter value type is incorrect
   */
  public TwoIndexTextParameter(ProtocolSpecification specification, int index1, int index2,
      String value) {
    super(specification, index1, index2);
    ParameterUtils.compatibleValueType(REQUIRED, specification);
    this.value = Objects.requireNonNull(value, "Value");
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public final String getValue() {
    return value;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    String encoded = encodedValue;
    if (encoded == null) {
      encoded = UrlEncoderHelper.encode(value);
      encodedValue = encoded;
    }
    return appendNameEquals(sb).append(encoded);
  }
}
