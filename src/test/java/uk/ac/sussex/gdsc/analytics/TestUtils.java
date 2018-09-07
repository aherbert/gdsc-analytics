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

import uk.ac.sussex.gdsc.analytics.parameters.CustomParameterSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.ParameterSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.ValueType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.rng.UniformRandomProvider;

/**
 * Contains utilities for testing
 */
public class TestUtils {

  private static final char[] NAME_CHARS;

  static {
    NAME_CHARS = new char[36];
    int i = 0;
    for (char c = 'a'; c <= 'z'; c++) {
      NAME_CHARS[i++] = c;
    }
    for (char c = '0'; c <= '9'; c++) {
      NAME_CHARS[i++] = c;
    }
  }

  private TestUtils() {}

  private static ExecutorService defaultExecutorService;
  static {
    defaultExecutorService =
        Executors.newFixedThreadPool(1, new BackgroundThreadFactory(Thread.NORM_PRIORITY));
  }


  /**
   * Creates the builder with the given tracking Id.
   * 
   * <p>Use a common executor service to avoid having to shutdown each test instance client.
   *
   * @param trackingId the tracking id
   * @return the google analytics client. builder
   */
  public static GoogleAnalyticsClient.Builder createBuilder(String trackingId) {
    return GoogleAnalyticsClient.createBuilder(trackingId)
        .setExecutorService(defaultExecutorService);
  }

  /**
   * Gets a random alphanumeric name.
   *
   * @param rg the random generator
   * @param length the length
   * @return the string
   */
  public static String randomName(UniformRandomProvider rg, int length) {
    char[] chars = new char[length];
    for (int i = 0; i < length; i++) {
      chars[i] = NAME_CHARS[rg.nextInt(NAME_CHARS.length)];
    }
    return new String(chars);
  }

  /**
   * Gets a random alphanumeric path. The path starts with '/' and has '/' inside the string.
   *
   * @param rg the random generator
   * @param length the length
   * @return the string
   */
  public static String randomPath(UniformRandomProvider rg, int length) {
    char[] chars = new char[length + 1];
    chars[0] = '/';
    for (int i = 1; i <= length; i++) {
      if (i % 3 == 0) {
        chars[i] = '/';
      } else {
        chars[i] = NAME_CHARS[rg.nextInt(NAME_CHARS.length)];
      }
    }
    return new String(chars);
  }

  /**
   * New boolean parameter specification.
   *
   * @param nameFormat the name format
   * @return the parameter specification
   */
  public static ParameterSpecification newBooleanParameterSpecification(String nameFormat) {
    return new CustomParameterSpecification("Custom Boolean", nameFormat, ValueType.BOOLEAN, 0);
  }

  /**
   * New int parameter specification.
   *
   * @param nameFormat the name format
   * @return the parameter specification
   */
  public static ParameterSpecification newIntParameterSpecification(String nameFormat) {
    return new CustomParameterSpecification("Custom Integer", nameFormat, ValueType.INTEGER, 0);
  }

  /**
   * New number parameter specification.
   *
   * @param nameFormat the name format
   * @return the parameter specification
   */
  public static ParameterSpecification newNumberParameterSpecification(String nameFormat) {
    return new CustomParameterSpecification("Custom Number", nameFormat, ValueType.NUMBER, 0);
  }

  /**
   * New currency number parameter specification.
   *
   * @param nameFormat the name format
   * @return the parameter specification
   */
  public static ParameterSpecification newCurrencyParameterSpecification(String nameFormat) {
    return new CustomParameterSpecification("Custom Currency", nameFormat, ValueType.CURRENCY, 0);
  }

  /**
   * New currency text parameter specification.
   *
   * @param nameFormat the name format
   * @return the parameter specification
   */
  public static ParameterSpecification newTextParameterSpecification(String nameFormat) {
    return new CustomParameterSpecification("Custom Text", nameFormat, ValueType.TEXT, 0);
  }

  /**
   * New currency text parameter specification.
   *
   * @param nameFormat the name format
   * @param length the length
   * @return the parameter specification
   */
  public static ParameterSpecification newTextParameterSpecification(String nameFormat,
      int length) {
    return new CustomParameterSpecification("Custom Text", nameFormat, ValueType.TEXT, length);
  }
}
