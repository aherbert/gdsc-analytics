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

import java.util.EnumMap;

/**
 * A factory for creating {@link IndexReplacer} objects.
 */
public final class IndexReplacerFactory {

  /** Cache replacers for all the parameters. */
  private static final EnumMap<ProtocolSpecification, IndexReplacer> map =
      new EnumMap<>(ProtocolSpecification.class);

  /**
   * No public creation.
   */
  private IndexReplacerFactory() {
    // Do nothing
  }

  /**
   * Creates the index replacer.
   * 
   * <p>This method is guarunteed to return a dedicated 1, 2, or 3 index replacer if required by the
   * specification.
   *
   * @param specification the specification
   * @return the index replacer
   * @see OneIndexReplacer
   * @see TwoIndexReplacer
   * @see ThreeIndexReplacer
   */
  public static IndexReplacer createIndexReplacer(ProtocolSpecification specification) {
    return map.computeIfAbsent(specification, IndexReplacerFactory::newReplacer);
  }

  /**
   * Create a new replacer.
   *
   * @param specification the specification
   * @return the index replacer
   */
  private static IndexReplacer newReplacer(ProtocolSpecification specification) {
    if (specification.getNumberOfIndexes() == 1) {
      return new OneIndexReplacer(specification);
    }
    if (specification.getNumberOfIndexes() == 2) {
      return new TwoIndexReplacer(specification);
    }
    // The API does not support more than 3 indexes
    return new ThreeIndexReplacer(specification);
  }
}
