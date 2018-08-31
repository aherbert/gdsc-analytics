/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics Measurement
 * protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert, Daniel Murphy
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * URI encoder to encode strings using UTF-8.
 */
public class URIEncoder {

    /** The characters that do not require encoding. */
    private final static boolean[] dontNeedEncoding;
    /** The space character. This is replaced with '+'. */
    private final static char SPACE = ' ';
    /** The plus '+' character. */
    private final static char PLUS = '+';

    static {

        // Copied from java.net.URLEncoder:

        /*
         * The list of characters that are not encoded has been determined as follows:
         *
         * RFC 2396 states:
         *
         * Data characters that are allowed in a URI but do not have a reserved purpose
         * are called unreserved. These include upper and lower case letters, decimal
         * digits, and a limited set of punctuation marks and symbols.
         *
         * unreserved = alphanum | mark
         *
         * mark = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
         *
         * Unreserved characters can be escaped without changing the semantics of the
         * URI, but this should not be done unless the URI is being used in a context
         * that does not allow the unescaped character to appear.
         *
         *
         * Note:
         *
         * It appears that both Netscape and Internet Explorer escape all special
         * characters from this list with the exception of "-", "_", ".", "*". While it
         * is not clear why they are escaping the other characters, perhaps it is safest
         * to assume that there might be contexts in which the others are unsafe if not
         * escaped. Therefore, we will use the same list. It is also noteworthy that
         * this is consistent with O'Reilly's "HTML: The Definitive Guide" (page 164).
         *
         * As a last note, Internet Explorer does not encode the "@" character which is
         * clearly not unreserved according to the RFC. We are being consistent with the
         * RFC in this matter, as is Netscape.
         */

        // The original code used a bitset but this has been switched here to an array
        dontNeedEncoding = new boolean[123];
        int i;
        for (i = 'a'; i <= 'z'; i++) // ASCII 97 - 122
            dontNeedEncoding[i] = true;
        for (i = 'A'; i <= 'Z'; i++) // ASCII 65 - 90
            dontNeedEncoding[i] = true;
        for (i = '0'; i <= '9'; i++) // ASCII 48 - 57
            dontNeedEncoding[i] = true;
        dontNeedEncoding[' '] = true; // ASCII 32
        dontNeedEncoding['-'] = true; // ASCII 45
        dontNeedEncoding['_'] = true; // ASCII 95
        dontNeedEncoding['.'] = true; // ASCII 46
        dontNeedEncoding['*'] = true; // ASCII 42
    }

    /**
     * No public instances
     */
    private URIEncoder() {
        // Do nothing
    }

    /**
     * Encode the string using UTF-8.
     *
     * <p>A check is made for any characters that require encoding. If {@code false}
     * the same string is returned.
     *
     * <p>Otherwise the actual encoding is performed by
     * {@link URLEncoder#encode(String, String)}.
     *
     * @param string The string
     * @return The encoded string
     */
    public static String encodeURI(String string) {
        if (noEncodingRequired(string))
            // Handle special case of space character
            return encodeSpace(string);
        return encodeURI(string, "UTF-8");
    }

    /**
     * Encode the string substituting the space character for '+'.
     *
     * @param string the string
     * @return the string
     */
    private static String encodeSpace(String string) {
        if (string.indexOf(SPACE) == -1)
            return string;
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++)
            if (chars[i] == SPACE)
                chars[i] = PLUS;
        return new String(chars);
    }

    /**
     * Encode the string using the given encoding.
     *
     * @param string   The string
     * @param encoding The encoding (UTF-8 is recommended)
     * @return The encoded string
     * @throws RuntimeException If the encoding is not supported
     */
    static String encodeURI(String string, String encoding) throws RuntimeException {
        try {
            return URLEncoder.encode(string, encoding);
        } catch (final UnsupportedEncodingException e) {
            // UTF-8 is required by the Java platform so this should not happen
            Logger.getLogger(URIEncoder.class.getName()).severe("Unsupported encoding: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the string contains only characters that will not be encoded.
     *
     * @param s the string
     * @return true, if no encoded characters are present
     */
    public static boolean noEncodingRequired(String s) {
        Objects.requireNonNull(s, "The string is null");
        for (int i = 0; i < s.length(); i++)
            if (!noEncodingRequired(s.charAt(i)))
                return false;
        return true;
    }

    /**
     * Check if the character will not be encoded.
     *
     * @param c the character
     * @return true, if no encoding is required
     */
    public static boolean noEncodingRequired(char c) {
        final int i = c;
        // Char is unsigned so no need to check below zero.
        return (i < dontNeedEncoding.length) ? dontNeedEncoding[i] : false;
    }
}
