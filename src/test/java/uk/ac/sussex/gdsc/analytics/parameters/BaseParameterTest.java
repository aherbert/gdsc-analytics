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
