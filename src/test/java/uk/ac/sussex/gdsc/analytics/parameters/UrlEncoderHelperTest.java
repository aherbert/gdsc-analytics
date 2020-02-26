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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class UrlEncoderHelperTest {

  private final Logger logger = Logger.getLogger(UrlEncoderHelperTest.class.getName());
  private final Level level = Level.FINE;

  @Test
  public void testEncoder() throws Exception {
    // Create a string that should not be encoded
    final StringBuilder sb = new StringBuilder();
    for (char c = '0'; c <= '9'; c++) {
      sb.append(c);
    }
    for (char c = 'a'; c <= 'z'; c++) {
      sb.append(c);
    }
    for (char c = 'A'; c <= 'Z'; c++) {
      sb.append(c);
    }
    sb.append("-_.*");
    final String unencoded = sb.toString();
    Assertions.assertTrue(unencoded.length() > 0);
    Assertions.assertTrue(UrlEncoderHelper.noEncodingRequired(unencoded));
    // The same string
    Assertions.assertSame(unencoded, UrlEncoderHelper.encode(unencoded));

    // Special case of space
    testEncode("with space");
    testEncode(" space");
    testEncode("with ");
    testEncode(" ");

    // Encode others as hex in UTF-8
    testEncode("&");
    testEncode("@");
    testEncode("/");
    testEncode("[]{}%$#");
    testEncode("normal []{}%$# text");
    testEncode("normal [a]b{c}d%e$f# text");
    testEncode("complex ἀ Ģ ↛");

    // Try and break it with extended characters in UTF-8
    testEncode((char) 0x0000);
    testEncode((char) 0x0001);
    testEncode((char) 0x0010);
    testEncode((char) 0x0100);
    testEncode((char) 0x1000);
    testEncode((char) 0x000F);
    testEncode((char) 0x00F0);
    testEncode((char) 0x0F00);
    testEncode((char) 0xF000);
    testEncode((char) 0xFFFF);
    testEncode((char) 0xFFF0);
    testEncode((char) 0xFF00);
    testEncode((char) 0xF000);
  }

  private void testEncode(String string) throws Exception {
    // The encoding is not standard. It encodes all unsupported
    // chars as hex. So encode and test it can be decoded to the
    // same string using a standard decoder.
    final String encoded = UrlEncoderHelper.encode(string);

    // Log the encoding
    logger.log(level, () -> {
      String answer;
      try {
        answer = URLEncoder.encode(string, "UTF-8");
      } catch (final UnsupportedEncodingException ex) {
        throw new RuntimeException(ex);
      }
      return "<<" + string + ">> == <<" + answer + ">> : <<" + encoded + ">>";
    });
    Assertions.assertEquals(string, decode(encoded));
  }

  private void testEncode(char ch) throws Exception {
    testEncode(new String(new char[] {ch}));
  }

  private static String decode(String string) throws Exception {
    return URLDecoder.decode(string, "UTF-8");
  }

  @Test
  public void testEncoderThrows() {
    Assertions.assertThrows(UnsupportedEncodingRuntimeException.class, () -> {
      UrlEncoderHelper.urlEncode("test", "not UTF-8!");
    });
  }
}
