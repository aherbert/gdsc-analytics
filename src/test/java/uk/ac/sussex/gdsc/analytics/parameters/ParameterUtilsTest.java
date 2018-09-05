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

import uk.ac.sussex.gdsc.analytics.TestUtils;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ParameterUtilsTest {

  private static String message;

  @BeforeAll
  public static void setup() {
    UniformRandomProvider rg = RandomSource.create(RandomSource.SPLIT_MIX_64);
    message = TestUtils.randomName(rg, 30);
  }

  @AfterAll
  public static void teardown() {
    message = null;
  }

  @Test
  public void testRequireNotEmpty() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireNotEmpty(null, message);
    }).getMessage());
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireNotEmpty("", message);
    }).getMessage());
  }

  @Test
  public void testRequirePositive() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requirePositive(-1, message);
    }).getMessage());
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requirePositive(-1L, message);
    }).getMessage());
    for (int i = 0; i < 5; i++) {
      Assertions.assertEquals(i, ParameterUtils.requirePositive(i, message));
      Assertions.assertEquals(i, ParameterUtils.requirePositive((long) i, message));
    }
  }

  @Test
  public void testRequireStrictlyPositive() {
    Assertions.assertEquals(message, Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ParameterUtils.requireStrictlyPositive(0, message);
    }).getMessage());
    for (int i = 1; i < 5; i++) {
      Assertions.assertEquals(i, ParameterUtils.requireStrictlyPositive(i, message));
    }
  }

  @Test
  public void testValidateTrackingId() {
    String good = "UA-1234-5";
    ParameterUtils.validateTrackingId(good);

    Assertions.assertThrows(NullPointerException.class, () -> {
      ParameterUtils.validateTrackingId(null);
    });

    for (String trackingId : new String[] {
        // Just bad
        "bad", "asdfasdf",
        // Empty
        "",
        // Wrong format
        "UAA-A-0", "UDHDHDH", "00-123-5",
        // Bad case
        good.toLowerCase(),
        // whitespace
        " " + good, good + " ",
        // Extra digits
        "U" + good, good + "9"}) {
      Assertions.assertThrows(IllegalArgumentException.class, () -> {
        ParameterUtils.validateTrackingId(trackingId);
      });
    }

    for (String trackingId : new String[] {"UA-1234-5", "UA-1234567-0"}) {
      ParameterUtils.validateTrackingId(trackingId);
    }
  }
}
