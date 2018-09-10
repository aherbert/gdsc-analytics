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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings("javadoc")
public class DefaultHttpUrlConnectionCallbackTest {
  @Test
  public void testProcess() throws IOException {
    DefaultHttpUrlConnectionCallback callback = new DefaultHttpUrlConnectionCallback();

    // Default properties
    Assertions.assertEquals(0, callback.getResponseCode());
    Assertions.assertNull(callback.getContentType());
    Assertions.assertNull(callback.getBytes());
    Assertions.assertNull(callback.getBytesAsText());

    // No bytes returned
    int responseCode = 1;
    byte[] bytes = new byte[0];
    HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);
    Mockito.doReturn(responseCode).when(connection).getResponseCode();
    Mockito.doReturn(new ByteArrayInputStream(bytes)).when(connection).getInputStream();

    callback.process(connection);

    Assertions.assertEquals(responseCode, callback.getResponseCode());
    Assertions.assertArrayEquals(bytes, callback.getBytes());
    Assertions.assertNull(callback.getContentType());
    Assertions.assertNull(callback.getBytesAsText());

    // Bad content
    String contentType = "image/gif";
    connection = Mockito.mock(HttpURLConnection.class);
    Mockito.doReturn(responseCode).when(connection).getResponseCode();
    Mockito.doReturn(new ByteArrayInputStream(bytes)).when(connection).getInputStream();
    Mockito.doReturn(contentType).when(connection).getContentType();

    callback.process(connection);

    Assertions.assertEquals(responseCode, callback.getResponseCode());
    Assertions.assertArrayEquals(bytes, callback.getBytes());
    Assertions.assertEquals(contentType, callback.getContentType());
    Assertions.assertNull(callback.getBytesAsText());

    // Good content
    responseCode = HttpURLConnection.HTTP_OK;
    String text = "This should be returned";
    bytes = text.getBytes(StandardCharsets.UTF_8);
    contentType = "blah; charset=utf-8";
    connection = Mockito.mock(HttpURLConnection.class);
    Mockito.doReturn(responseCode).when(connection).getResponseCode();
    Mockito.doReturn(new ByteArrayInputStream(bytes)).when(connection).getInputStream();
    Mockito.doReturn(contentType).when(connection).getContentType();

    callback.process(connection);

    Assertions.assertEquals(responseCode, callback.getResponseCode());
    Assertions.assertArrayEquals(bytes, callback.getBytes());
    Assertions.assertEquals(contentType, callback.getContentType());
    Assertions.assertEquals(text, callback.getBytesAsText());

    callback.reset();

    // Default properties
    Assertions.assertEquals(0, callback.getResponseCode());
    Assertions.assertNull(callback.getContentType());
    Assertions.assertNull(callback.getBytes());
    Assertions.assertNull(callback.getBytesAsText());
  }
}
