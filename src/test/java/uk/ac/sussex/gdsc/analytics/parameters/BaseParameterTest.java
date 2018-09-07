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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This BaseParameter class is abstract. But has some functionality that can be tested with a dummy
 * sub-class. .
 */
@SuppressWarnings("javadoc")
public class BaseParameterTest {

  private static class MockBaseParameter extends BaseParameter {
    public MockBaseParameter(ParameterSpecification specification) {
      super(specification);
    }

    public MockBaseParameter(ProtocolSpecification specification) {
      super(specification);
    }

    @Override
    public StringBuilder formatTo(StringBuilder sb) {
      return sb;
    }

    @Override
    protected StringBuilder appendNameEquals(StringBuilder sb) {
      return sb;
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new MockBaseParameter((ParameterSpecification) null);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new MockBaseParameter((ProtocolSpecification) null);
    });
  }

  @Test
  public void testIsProtocolSpecification() {
    MockBaseParameter param = new MockBaseParameter(ProtocolSpecification.PROTOCOL_VERSION);
    Assertions.assertTrue(param.isProtocolSpecification());

    ParameterSpecification spec = ProtocolSpecification.PROTOCOL_VERSION;
    param = new MockBaseParameter(spec);
    Assertions.assertTrue(param.isProtocolSpecification());

    spec = TestUtils.newBooleanParameterSpecification("name");
    param = new MockBaseParameter(spec);
    Assertions.assertFalse(param.isProtocolSpecification());
  }
}
