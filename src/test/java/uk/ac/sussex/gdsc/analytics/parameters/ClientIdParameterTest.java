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

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ClientIdParameterTest {
  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
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
  public void testFormat() {
    String uuid1 = "123e4567-e89b-12d3-a456-426655440000";
    String uuid2 = "00112233-4455-6677-8899-aabbccddeeff";
    for (String clientId : new String[] {uuid1, uuid2}) {
      Assertions.assertEquals(String.format("&cid=%s", clientId),
          TestUtils.callAppendTo(new ClientIdParameter(clientId)));
      Assertions.assertEquals(String.format("&cid=%s", clientId),
          TestUtils.callAppendTo(new ClientIdParameter(UUID.fromString(clientId))));
    }
  }
}
