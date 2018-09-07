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

import java.util.Objects;

/**
 * Base class to implements the {@link FormattedParameter} interface for a
 * {@link ParameterSpecification}.
 */
abstract class BaseParameter implements FormattedParameter {

  /** The parameter specification. */
  private final ParameterSpecification specification;

  /**
   * A reference to the parameter specification specification if it is a {@link ProtocolSpecification}.
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
        (specification instanceof ProtocolSpecification) ? (ProtocolSpecification) specification : null;
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
    return protocolSpecification != null;
  }

  /**
   * Append "{@code name=}" to the {@link StringBuilder}. The '{@code =}' (equals) character must be
   * included.
   * 
   * This is the {@code name} component of a {@code name=value} pair for the
   * {@link FormattedParameter#formatTo(StringBuilder)} interface method.
   * 
   * <p>The {@code name} is expected be a correctly formatted parameterSpecification key for a URL, i.e. special
   * characters must be encoded.
   *
   * @param sb the string builder
   * @return the string builder
   */
  protected abstract StringBuilder appendNameEquals(StringBuilder sb);
}
