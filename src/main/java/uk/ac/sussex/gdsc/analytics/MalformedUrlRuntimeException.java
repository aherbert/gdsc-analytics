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
