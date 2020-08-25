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

package uk.ac.sussex.gdsc.analytics;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings("javadoc")
class DefaultHttpUrlConnectionCallbackTest {
  @Test
  void testProcess() throws IOException {
    final DefaultHttpUrlConnectionCallback callback = new DefaultHttpUrlConnectionCallback();

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
    connection = Mockito.mock(HttpURLConnection.class);
    String contentType = "image/gif";
    Mockito.doReturn(responseCode).when(connection).getResponseCode();
    Mockito.doReturn(new ByteArrayInputStream(bytes)).when(connection).getInputStream();
    Mockito.doReturn(contentType).when(connection).getContentType();

    callback.process(connection);

    Assertions.assertEquals(responseCode, callback.getResponseCode());
    Assertions.assertArrayEquals(bytes, callback.getBytes());
    Assertions.assertEquals(contentType, callback.getContentType());
    Assertions.assertNull(callback.getBytesAsText());

    // Good content
    connection = Mockito.mock(HttpURLConnection.class);
    responseCode = HttpURLConnection.HTTP_OK;
    final String text = "This should be returned";
    bytes = text.getBytes(StandardCharsets.UTF_8);
    contentType = "blah; charset=utf-8";
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
