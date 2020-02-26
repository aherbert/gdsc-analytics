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

import java.util.Objects;

/**
 * Base class to implements the {@link FormattedParameter} interface for a
 * {@link ParameterSpecification}.
 */
abstract class BaseParameter implements FormattedParameter {

  /** Used to assigned null to the protocol specification. */
  private static final ProtocolSpecification NONE = null;

  /** The parameter specification. */
  private final ParameterSpecification specification;

  /**
   * A reference to the parameter specification specification if it is a
   * {@link ProtocolSpecification}.
   *
   * <p>Used when a {@link ProtocolSpecification} was passed in the constructor.
   *
   * <p>Protected to allow package sub-class to handle the defined API and custom specifications
   * differently.
   */
  protected final ProtocolSpecification protocolSpecification;

  /**
   * Create a new instance.
   *
   * @param specification the specification specification
   */
  public BaseParameter(ParameterSpecification specification) {
    this.specification = Objects.requireNonNull(specification, "Parameter");
    this.protocolSpecification =
        (specification instanceof ProtocolSpecification) ? (ProtocolSpecification) specification
            : NONE;
  }

  /**
   * Create a new instance.
   *
   * @param specification the specification specification
   */
  public BaseParameter(ProtocolSpecification specification) {
    this.specification = Objects.requireNonNull(specification, "Parameter");
    this.protocolSpecification = specification;
  }

  /**
   * Gets the parameter specification.
   *
   * <p>This may be a {@link ProtocolSpecification} instance indicating that the specification is
   * part of the core API.
   *
   * <p>Otherwise this is a custom parameter specification.
   *
   * @return the parameter specification
   * @see #isProtocolSpecification()
   */
  public final ParameterSpecification getParameterSpecification() {
    return specification;
  }

  /**
   * Checks if the {@link ParameterSpecification} is a {@link ProtocolSpecification}.
   *
   * <p>If false then the {@link ParameterSpecification} is a custom implementation not defined
   * within {@link ProtocolSpecification} API reference.
   *
   * @return true if a protocol specification
   * @see #getParameterSpecification()
   */
  public final boolean isProtocolSpecification() {
    return protocolSpecification != NONE;
  }

  /**
   * Append "{@code name=}" to the {@link StringBuilder}. The '{@code =}' (equals) character must be
   * included.
   *
   * <p>This is the {@code name} component of a {@code name=value} pair for the
   * {@link FormattedParameter#formatTo(StringBuilder)} interface method.
   *
   * <p>The {@code name} is expected be a correctly formatted parameterSpecification key for a URL,
   * i.e. special characters must be encoded.
   *
   * @param sb the string builder
   * @return the string builder
   */
  protected abstract StringBuilder appendNameEquals(StringBuilder sb);
}
