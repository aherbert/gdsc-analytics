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

package uk.ac.sussex.gdsc.analytics.parameters;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
class ClientIdParameterTest {
  @SuppressWarnings("unused")
  @Test
  void testConstructor() {
    Assertions.assertThrows(NullPointerException.class, () -> {
      new ClientIdParameter((String) null);
    });
    Assertions.assertThrows(NullPointerException.class, () -> {
      new ClientIdParameter((UUID) null);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new ClientIdParameter("not-a-UUID");
    });
  }

  @Test
  void testFormat() {
    final String uuid1 = "123e4567-e89b-12d3-a456-426655440000";
    final String uuid2 = "00112233-4455-6677-8899-aabbccddeeff";
    for (final String clientId : new String[] {uuid1, uuid2}) {
      Assertions.assertEquals(String.format("cid=%s", clientId),
          new ClientIdParameter(clientId).format());
      Assertions.assertEquals(String.format("cid=%s", clientId),
          new ClientIdParameter(UUID.fromString(clientId)).format());
    }
  }
}
