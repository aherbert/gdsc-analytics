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

import java.io.UnsupportedEncodingException;

/**
 * The Character Encoding is not supported.
 * 
 * <p>This class serves to wrap a checked {@link UnsupportedEncodingException} with an unchecked
 * exception.
 */
class UnsupportedEncodingRuntimeException extends RuntimeException {

  /**
   * The serial version ID.
   */
  private static final long serialVersionUID = 329458356137801805L;

  /**
   * Constructs an UnsupportedEncodingRuntimeException with the underlying
   * {@link UnsupportedEncodingException}.
   *
   * @param ex the underlying exception
   */
  public UnsupportedEncodingRuntimeException(UnsupportedEncodingException ex) {
    super(ex);
  }
}
