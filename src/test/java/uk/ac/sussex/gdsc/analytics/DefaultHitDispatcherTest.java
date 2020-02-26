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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This uses the mockito framework to test the dispatch of requests.
 */
@SuppressWarnings("javadoc")
public class DefaultHitDispatcherTest {

  @SuppressWarnings("unused")
  @Test
  public void testConstructor() {
    // Just test that null can be explicitly used for the proxy
    new DefaultHitDispatcher(createUrl(), (Proxy) null);
  }

  @Test
  public void testProperties() {
    final DefaultHitDispatcher hitDispatcher = new DefaultHitDispatcher(createUrl());

    // Initialised OK
    Assertions.assertThat(hitDispatcher.isDisabled()).isFalse();
    Assertions.assertThat(hitDispatcher.getLastIoException()).isNull();

    // Can stop
    Assertions.assertThat(hitDispatcher.stop()).isTrue();
    Assertions.assertThat(hitDispatcher.isDisabled()).isTrue();

    // Can start
    Assertions.assertThat(hitDispatcher.start()).isTrue();
    Assertions.assertThat(hitDispatcher.isDisabled()).isFalse();
  }

  private static URL createUrl() {
    try {
      return new URL("http", "www.abc.com", "/file");
    } catch (final MalformedURLException ex) {
      ex.printStackTrace();
      Assertions.fail(ex.getMessage());
    }
    return null; // For the compiler
  }

  @Test
  public void testSend() throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
    final HttpURLConnection urlConnection = createHttpUrlConnection(HttpURLConnection.HTTP_OK, out);
    final HttpConnectionProvider provider = new HttpConnectionProvider() {
      @Override
      public HttpURLConnection openConnection(URL url, Proxy proxy) {
        return urlConnection;
      }
    };

    final DefaultHitDispatcher hitDispatcher = new DefaultHitDispatcher(createUrl(), provider);

    // When disabled
    hitDispatcher.stop();
    final String hit = "dummy";
    DispatchStatus status = hitDispatcher.send(hit, 0);
    Assertions.assertThat(status).isEqualTo(DispatchStatus.DISABLED);

    // A standard send hit
    hitDispatcher.start();

    final Logger logger = Logger.getLogger(DefaultHitDispatcher.class.getName());
    logger.setLevel(Level.FINE);

    status = hitDispatcher.send(hit, 0);
    Assertions.assertThat(status).isEqualTo(DispatchStatus.COMPLETE);

    // Check the output stream
    Assertions.assertThat(out.size()).isNotEqualTo(0);

    // Check interaction with the connection
    Mockito.verify(urlConnection, Mockito.times(1)).setRequestMethod("POST");
    Mockito.verify(urlConnection, Mockito.times(1)).setDoOutput(true);
    Mockito.verify(urlConnection, Mockito.times(1)).setUseCaches(false);
    Mockito.verify(urlConnection, Mockito.times(1)).setFixedLengthStreamingMode(out.size());

    // Check the content of the POST
    String parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
    Assertions.assertThat(parameters).isEqualTo(hit);

    // With a timestamp
    logger.setLevel(Level.INFO);
    out.reset();
    status = hitDispatcher.send(hit, System.currentTimeMillis());
    Assertions.assertThat(status).isEqualTo(DispatchStatus.COMPLETE);

    parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
    Assertions.assertThat(parameters).contains(hit);
    Assertions.assertThat(parameters).contains("qt=");

    // With a timestamp and the hit is a StringBuilder
    out.reset();
    status = hitDispatcher.send(new StringBuilder(hit), System.currentTimeMillis());
    Assertions.assertThat(status).isEqualTo(DispatchStatus.COMPLETE);

    parameters = new String(out.toByteArray(), StandardCharsets.UTF_8);
    Assertions.assertThat(parameters).contains(hit);
    Assertions.assertThat(parameters).contains("qt=");
  }

  private static HttpURLConnection createHttpUrlConnection(int responseCode,
      ByteArrayOutputStream out) throws IOException {
    // The mock object will not do anything
    final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);

    // The response
    Mockito.when(urlConnection.getResponseCode()).thenReturn(responseCode);

    // Provide space for the POST request
    Mockito.when(urlConnection.getOutputStream()).thenReturn(out);
    return urlConnection;
  }

  @Test
  public void testSendWithBadResponseCode() throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
    final HttpURLConnection urlConnection =
        createHttpUrlConnection(HttpURLConnection.HTTP_INTERNAL_ERROR, out);
    final HttpConnectionProvider provider = new HttpConnectionProvider() {
      @Override
      public HttpURLConnection openConnection(URL url, Proxy proxy) {
        return urlConnection;
      }
    };

    final DefaultHitDispatcher hitDispatcher = new DefaultHitDispatcher(createUrl(), provider);

    final String hit = "dummy";
    final DispatchStatus status = hitDispatcher.send(hit, 0);
    Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);

    // This should not disable the dispatcher
    Assertions.assertThat(hitDispatcher.isDisabled()).isFalse();
  }

  @Test
  public void testSendWithUnknownHostException() {
    final IOException exception = new UnknownHostException("Test failed openConnection");
    final HttpConnectionProvider provider = new HttpConnectionProvider() {
      @Override
      public HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        throw exception;
      }
    };

    final DefaultHitDispatcher hitDispatcher = new DefaultHitDispatcher(createUrl(), provider);

    final String hit = "dummy";
    final DispatchStatus status = hitDispatcher.send(hit, 0);
    Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);

    // This should not disable the dispatcher
    Assertions.assertThat(hitDispatcher.isDisabled()).isTrue();
    Assertions.assertThat(hitDispatcher.getLastIoException()).isSameAs(exception);
  }

  @Test
  public void testSendWithIoException() throws IOException {

    final IOException exception = new IOException("Test failed getOutputStream");

    final HttpURLConnection urlConnection = Mockito.mock(HttpURLConnection.class);
    Mockito.when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
    Mockito.when(urlConnection.getOutputStream()).thenThrow(exception);

    final HttpConnectionProvider provider = new HttpConnectionProvider() {
      @Override
      public HttpURLConnection openConnection(URL url, Proxy proxy) {
        return urlConnection;
      }
    };

    final DefaultHitDispatcher hitDispatcher = new DefaultHitDispatcher(createUrl(), provider);

    final String hit = "dummy";
    final DispatchStatus status = hitDispatcher.send(hit, 0);
    Assertions.assertThat(status).isEqualTo(DispatchStatus.ERROR);

    // This should not disable the dispatcher
    Assertions.assertThat(hitDispatcher.isDisabled()).isTrue();
    Assertions.assertThat(hitDispatcher.getLastIoException()).isSameAs(exception);
  }
}
