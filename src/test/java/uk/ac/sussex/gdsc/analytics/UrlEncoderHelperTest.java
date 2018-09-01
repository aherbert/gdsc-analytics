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

  private void testEncode(char c) throws Exception {
    testEncode(new String(new char[] {c}));
  }

  private static String decode(String s) throws Exception {
    return URLDecoder.decode(s, "UTF-8");
  }

  @Test
  public void testEncoderThrows() {
    Assertions.assertThrows(RuntimeException.class, () -> {
      UrlEncoderHelper.urlEncode("test", "bad charset name");
    });
  }
}
