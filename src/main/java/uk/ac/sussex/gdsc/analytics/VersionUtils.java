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

/**
 * Store the version of the code using Semantic Versioning.
 *
 * <p>The major/minor version number will be updated when significant functionality has changed.
 * Otherwise the patch version will be incremented.
 *
 * <p>Note that this is the version of the uk.ac.sussex.gdsc.analytics package. It may be different
 * from the Maven version for the gdsc-analytics artifact.
 *
 * @see "http://semver.org/"
 */
public final class VersionUtils {

  /** The major version. */
  public static final int MAJOR = 2;

  /** The minor version. */
  public static final int MINOR = 0;

  /** The patch version. */
  public static final int PATCH = 0;

  /** The major version string. */
  public static final String VERSION_X;
  /**
   * The major.minor version string.
   */
  public static final String VERSION_X_X;
  /**
   * The major.minor.patch version string.
   */
  public static final String VERSION_X_X_X;

  /** Define level 1. */
  private static final int LEVEL_ONE = 1;
  /** Define level 2. */
  private static final int LEVEL_TWO = 2;

  static {
    VERSION_X = getVersion(1);
    VERSION_X_X = getVersion(2);
    VERSION_X_X_X = getVersion(3);
  }

  /**
   * Do not allow public construction.
   */
  private VersionUtils() {
    // Do nothing
  }

  /**
   * Get the version as a string. The string is built as major.minor.patch using the specified
   * number of levels.
   *
   * @param levels The number of levels (1-3).
   * @return The version
   */
  public static String getVersion(int levels) {
    final StringBuilder version = new StringBuilder().append(MAJOR);
    if (levels > LEVEL_ONE) {
      version.append('.').append(MINOR);
    }
    if (levels > LEVEL_TWO) {
      version.append('.').append(PATCH);
    }
    return version.toString();
  }
}
