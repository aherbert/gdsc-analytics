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

import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class IpAddressUtilsTest {

  @Test
  public void testIsIpAddress() {
    //@formatter:off
    for (final String ipAddress : new String[] {
        // Just bad
        "bad", "asdfasdf",
        // Empty
        "",
        /////////
        // IPv4
        /////////
        // OK
        "0.0.0.0",
        "1.2.3.4",
        "15.25.65.95",
        "100.200.10.20",
        "255.255.255.255",
        // Only 3 numbers
        "1.1.1",
        // Illegal ending with a dot
        "1.1.1.",
        // Illegal two consecutive dots
        "1..1.1",
        // Number exceeds 255
        "1.1.1.256",
        "1.1.256.1",
        "1.256.1.1",
        "256.1.1.1",
        // Illegal Negative number
        "1.1.1.-1",
        "1.1.-1.1",
        "1.-1.1.1",
        "-1.1.1.1",
        // Illegal Octal number
        "1.1.1.01",
        "1.1.01.1",
        "1.01.1.1",
        "01.1.1.1",
        // Illegal Hex number
        "1.1.1.a",
        "1.1.a.1",
        "1.a.1.1",
        "a.1.1.1",
        /////////
        // IPv6
        /////////
        // OK
        "0:0:0:0:0:0:0:0",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        // Leading zeros allowed
        "01:0:0:0:0:0:0:0",
        "0:01:0:0:0:0:0:0",
        "0:0:0:0:0:0:0:01",
        "00001:0:0:0:0:0:0:0",
        "0:00001:0:0:0:0:0:0",
        "0:0:0:0:0:0:0:00001",
        // Skip start
        "::FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "::FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "::FFFF:FFFF:FFFF:FFFF:FFFF:",
        // Skip end
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF::",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF::",
        "FFFF:FFFF:FFFF:FFFF:FFFF::",
        // Skip middle
        "FFFF:FFFF:FFFF::FFFF:FFFF:FFFF",
        "FFFF:FFFF::FFFF:FFFF",
        // Special case examples
        "::1",
        "2001:db8::1",
        "::192.168.0.1",
        "::ffff:192.168.0.1",
        // Illegal colon after .
        "::192.168.0.1:FFFF:FFFF",
        // Too long
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        // Too long with skip
        "::FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "::FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF::",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF::",
        "FFFF:FFFF:FFFF::FFFF:FFFF:FFFF:FFFF:FFFF",
        // Too short
        ":",
        // Bad skip index - ^: requires ^::
        ":FFFF:FFFF:FFFF::FFFF:FFFF:FFFF",
        // Bad skip index - :$ requires ::$
        "FFFF:FFFF:FFFF:FFFF::FFFF:FFFF:",
        // Multiple :: characters
        "FFFF::FFFF::FFFF:FFFF:FFFF:FFFF",
        "::FFFF::",
        // Missing leading or trailing number
        ":FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
        "FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:",
        // Bad Hex number
        "0:0:0:0:0:0:0:G",
        "0:0:0:0:0:0:G:0",
        "G:0:0:0:0:0:0:0",
        // Hex number too high (10000 = FFFF + 1 in hex digits)
        "0:0:0:0:0:0:0:10000",
        "10000:0:0:0:0:0:0:0",
        "0:0:0:0:10000:0:0:0",
        // Hex number too high before/after skip
        "10000:0:0::0:0",
        "0:0::0:10000",
        // Bad IPv4 address
        "::192.168.0.256",
    }) {
      //@formatter:on
      // Use Commons Validator to get the answer.
      // This could use Guava (which is what the code is based on)
      // e.g. InetAddresses.isInetAddress(ipAddress)
      // but that allows "::0FFFF".
      // This is a known bug: https://github.com/google/guava/issues/1604
      boolean expected = InetAddressValidator.getInstance().isValid(ipAddress);
      Assertions.assertEquals(expected, IpAddressUtils.isIpAddress(ipAddress), ipAddress);
    }
  }
}
