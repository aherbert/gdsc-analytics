/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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
 * Defines hit type values for the Google Analytics Measurement Protocol.
 *
 * @see <a href="http://goo.gl/a8d4RP#t">Hit type</a>
 */
public enum HitType {
  /** The pageview hit-type. */
  PAGEVIEW("pageview"),
  /** The screenview hit-type. */
  SCREENVIEW("screenview"),
  /** The event hit-type. */
  EVENT("event"),
  /** The transaction hit-type. */
  TRANSACTION("transaction"),
  /** The item hit-type. */
  ITEM("item"),
  /** The social hit-type. */
  SOCIAL("social"),
  /** The exception hit-type. */
  EXCEPTION("exception"),
  /** The timing hit-type. */
  TIMING("timing"),;

  /** The name. */
  private final String name;

  /**
   * Instantiates a new hit type.
   *
   * @param name the name
   */
  HitType(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return name;
  }
}
