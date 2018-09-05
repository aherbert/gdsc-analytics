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

package uk.ac.sussex.gdsc.analytics;

import java.net.MalformedURLException;

/**
 * Thrown to indicate that a malformed URL has occurred. Either no legal protocol could be found in
 * a specification string or the string could not be parsed.
 *
 * <p>This class serves to wrap a checked {@link MalformedURLException} with an unchecked exception.
 * It is used to avoid having to explicitly catch the {@link MalformedURLException} when the URL has
 * not been adjusted from known defaults.
 */
class MalformedUrlRuntimeException extends RuntimeException {

  /**
   * The serial version ID.
   */
  private static final long serialVersionUID = -27315935928218977L;

  /**
   * Constructs an MalformedUrlRuntimeException with the underlying {@link MalformedURLException}.
   *
   * @param ex the underlying exception
   */
  public MalformedUrlRuntimeException(MalformedURLException ex) {
    super(ex);
  }
}
