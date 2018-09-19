/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information about a Java application.
 * %%
 * Copyright (C) 2010 - 2018 Alex Herbert
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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains utility functions for checking IP addresses.
 */
public final class IpAddressUtils {

  /**
   * The regular expression for a single group of an IPv4 address.
   *
   * <p>Matches 0-255.
   */
  private static final String IPV4_SINGLE_GROUP_REGEX =
      "(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)";

  /** The regular expression for IPv4 address. */
  //@formatter:off
  public static final String IPV4_REGEX = "^"
      + IPV4_SINGLE_GROUP_REGEX + "\\."
      + IPV4_SINGLE_GROUP_REGEX + "\\."
      + IPV4_SINGLE_GROUP_REGEX + "\\."
      + IPV4_SINGLE_GROUP_REGEX + "$";
  //@formatter:on

  /** The compiled pattern for IPv4 address. */
  private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

  /** Maximum value of an unsigned short. */
  private static final int MAX_UNSIGNED_SHORT = 0xffff;

  /**
   * The '<strong>{@code .}</strong>' (dot) character.
   */
  private static final char DOT = '.';
  /**
   * The '<strong>{@code :}</strong>' (colon) character.
   */
  private static final char COLON = ':';

  /** Base 16 constant. */
  private static final int BASE_16 = 16;

  /** Number of groups (separated by .) in an IPV4 address. */
  private static final int IPV4_GROUPS = 4;

  /** Min number of hex groups (separated by :) in an IPV6 address. */
  private static final int IPV6_MIN_PART_COUNT = 3;

  /** Max number of hex groups (separated by :) in an IPV6 address. */
  private static final int IPV6_PART_COUNT = 8;

  /**
   * Use to indicate a bad index. Set to -2.
   */
  private static final int BAD_INDEX = -2;

  /**
   * Use to indicate no index. Set to -1.
   */
  private static final int NO_INDEX = -1;

  /**
   * Use to indicate one part. Set to 1.
   */
  private static final int ONE_PART = 1;

  /**
   * No public construction.
   */
  private IpAddressUtils() {
    // Do nothing
  }

  //@formatter:off
  /**
   * Checks if the IP address is a valid IPv4 or IPv6 address, e.g.:
   *
   * <ul>
   * <li>0-255 . 0-255 . 0-255 . 0-255 (IPv4)
   * <li>FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF (IPv6)
   * <li>::1 (IPv6 loopback address)
   * <li>2001:db8::1 (IPv6 reserved documentation prefix)
   * <li>::192.168.0.1 (IPv6 "IPv4 compatible" (or "compat") address)
   * <li>::ffff:192.168.0.1 (IPv6 "IPv4 mapped" address)
   * </ul>
   *
   * @param ipAddress the ip address
   * @return true, if valid
   */
  //@formatter:on
  public static boolean isIpAddress(String ipAddress) {
    // Based upon:
    // com.google.common.net.InetAddresses#ipStringToBytes
    // Altered to use a regex for IPv4 address.
    // Altered to refactor the check for IPv6 address into methods.

    // The string must contain only . : or hex digits
    boolean hasColon = false;
    boolean hasDot = false;
    for (int i = 0; i < ipAddress.length(); i++) {
      final char ch = ipAddress.charAt(i);
      if (ch == DOT) {
        hasDot = true;
      } else if (ch == COLON) {
        if (hasDot) {
          return false; // Colons must not appear after dots.
        }
        hasColon = true;
      } else if (Character.digit(ch, BASE_16) == -1) {
        return false; // Everything else must be a decimal or hex digit.
      }
    }

    if (hasColon) {
      return isAnyIpV6(ipAddress, hasDot);
    }
    if (hasDot) {
      return isIpV4(ipAddress);
    }
    return false;
  }

  //@formatter:off
  /**
   * Checks if the IP address is a valid IPv6 address, e.g.:
   *
   * <ul>
   * <li>FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF (IPv6)
   * <li>::1 (IPv6 loopback address)
   * <li>2001:db8::1 (IPv6 reserved documentation prefix)
   * <li>::192.168.0.1 (IPv6 "IPv4 compatible" (or "compat") address)
   * <li>::ffff:192.168.0.1 (IPv6 "IPv4 mapped" address)
   * </ul>
   *
   * @param ipAddress the ip address
   * @param withIpV4 True if the last entry is potentially an IPv4 address
   * @return true, if valid
   */
  //@formatter:on
  private static boolean isAnyIpV6(String ipAddress, boolean withIpV4) {
    // IPv6
    if (withIpV4) {
      // Contains IPv4 as the last entry:
      // :::::x.x.x.x
      final int lastColon = ipAddress.lastIndexOf(COLON);
      // Check for a valid IPv4 address
      final Matcher matcher = IPV4_PATTERN.matcher(ipAddress.substring(lastColon + 1));
      if (!matcher.matches()) {
        return false;
      }
      // Convert the IPv4 to a pair of hex entries, e.g. FFFF:FFFF
      // then add to the initial IPv6 section
      return isIpV6(ipAddress.substring(0, lastColon + 1) + convertIpV4ToHex(matcher));
    }
    return isIpV6(ipAddress);
  }

  /**
   * Convert an IPv4 address to hex using the groups from the matcher.
   *
   * <p>E.g. 255.255.255.255 = FFFF:FFFF
   *
   * @param matcher the pattern matcher for the IPv4 address
   * @return the hex representation
   */
  private static String convertIpV4ToHex(Matcher matcher) {
    final byte[] bytes = new byte[IPV4_GROUPS];
    for (int i = 0; i < IPV4_GROUPS; i++) {
      // Each value is in the range [0-255].
      // No NumberFormatException is expected.
      bytes[i] = (byte) (Integer.parseInt(matcher.group(i + 1)) & 0xff);
    }
    final String first = Integer.toHexString(((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
    final String second = Integer.toHexString(((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
    return first + ":" + second;
  }

  //@formatter:off
  /**
   * Checks if the IP address is a valid IPv6 address, e.g.:
   *
   * <ul>
   * <li>FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF
   * <li>::1 (IPv6 loopback address)
   * <li>2001:db8::1 (IPv6 reserved documentation prefix)
   * </ul>
   *
   * <p>This does not support checking for IPv4 address as the last entry.
   *
   * <p>This is a conversion of the Google method
   * {@code com.google.common.net.InetAddresses#textToNumericFormatV6(string)}
   * to remove dependencies and return a simple true or false.
   *
   * @param ipAddress the ip address
   * @return true, if valid
   */
  //@formatter:on
  private static boolean isIpV6(String ipAddress) {
    // Based upon:
    // com.google.common.net.InetAddresses#textToNumericFormatV6
    // The code has been refactored to reduce complexity.
    // This involved moving nested statements to functions.

    // An address can have [2..8] colons, and N colons make N+1 parts.
    // Note 8 colons only occurs when :: is at the start or end to 
    // denote a skip sequence.
    final String[] parts = splitIpV6(ipAddress);
    if (parts.length < IPV6_MIN_PART_COUNT || parts.length > IPV6_PART_COUNT + 1) {
      return false;
    }

    // Find the skip control sequence "::".
    // This indicates that a run of zeroes has been skipped.
    final int skipIndex = getSkipIndex(parts);
    if (skipIndex == BAD_INDEX) {
      // More than one skip control sequence
      return false;
    }

    if (skipIndex == NO_INDEX) {
      return isValidCompleteIpV6(parts);
    }

    final int partsBefore = getPartsBefore(parts, skipIndex);
    if (partsBefore == BAD_INDEX) {
      return false;
    }

    final int partsAfter = getPartsAfter(parts, skipIndex);
    if (partsAfter == BAD_INDEX) {
      return false;
    }

    // If we found a ::, then we must have skipped at least one part.
    final int partsSkipped = IPV6_PART_COUNT - (partsBefore + partsAfter);
    if (partsSkipped < ONE_PART) {
      return false;
    }

    // Now check the hextets in the parts before & after the skip ::
    return isValidCountUp(parts, partsBefore) && isValidCountDown(parts, partsAfter);
  }

  /**
   * Gets the number of parts before the ::.
   *
   * @param parts the parts
   * @param skipIndex the skip index
   * @return the parts before
   */
  private static int getPartsBefore(String[] parts, int skipIndex) {
    int partsBefore = skipIndex;
    // Check for a : at the start
    if (parts[0].length() == 0) {
      // ^: requires ^::
      // Check there is only one part before the skip
      if (partsBefore != ONE_PART) {
        return BAD_INDEX;
      }
      // No need to check before
      partsBefore = 0;
    }
    return partsBefore;
  }

  private static int getPartsAfter(String[] parts, int skipIndex) {
    int partsAfter = parts.length - skipIndex - 1;
    // Check for a : at the end
    if (parts[parts.length - 1].length() == 0) {
      // :$ requires ::$
      // Check there is only one part after the skip
      if (partsAfter != ONE_PART) {
        return BAD_INDEX;
      }
      // No need to check after 
      partsAfter = 0;
    }
    return partsAfter;
  }

  /**
   * Split the IP address using the colon character.
   *
   * <p>This is preferred over {@link String#split(String, int)} as that drops ending empty
   * segments.
   *
   * @param ipAddress the ip address
   * @return the string parts
   */
  private static String[] splitIpV6(String ipAddress) {
    // At an extra 2 since the validity test uses parts.length > IPV6_PART_COUNT + 1
    final String[] parts = new String[IPV6_PART_COUNT + 2];
    int count = 0;
    int start = 0;
    int end = ipAddress.indexOf(COLON);
    while (end >= 0) {
      parts[count++] = ipAddress.substring(start, end);
      // Check capacity
      if (count == parts.length) {
        return parts;
      }
      start = end + 1;
      end = ipAddress.indexOf(COLON, start);
    }
    parts[count++] = ipAddress.substring(start);
    return Arrays.copyOf(parts, count);
  }

  /**
   * Gets the skip index, i.e. the location in the parts array which is empty.
   *
   * <p>This ignores empty parts so the index is in the range 1 to parts.length - 2.
   *
   * <p>If multiple parts are empty this returns {@link #BAD_INDEX}.
   *
   * @param parts the parts
   * @return the skip index
   */
  private static int getSkipIndex(String[] parts) {
    int skipIndex = NO_INDEX;
    // Disregarding the endpoints, find "::" with nothing in between.
    // This indicates that a run of zeroes has been skipped.
    for (int i = 1; i < parts.length - 1; i++) {
      if (parts[i].length() == 0) {
        if (skipIndex != NO_INDEX) {
          return BAD_INDEX; // Can't have more than one ::
        }
        skipIndex = i;
      }
    }
    return skipIndex;
  }


  /**
   * Checks all the parts are valid for a complete IPv6 address.
   *
   * @param parts the parts
   * @return true if valid
   */
  private static boolean isValidCompleteIpV6(final String[] parts) {
    // We must have exactly the right number of parts.
    if (parts.length != IPV6_PART_COUNT) {
      return false;
    }
    return isValidCountUp(parts, IPV6_PART_COUNT);
  }

  /**
   * Checks all the parts are valid for {@code 0 <= i < count}.
   *
   * @param parts the parts
   * @param count the count
   * @return true if valid
   */
  private static boolean isValidCountUp(final String[] parts, int count) {
    for (int i = 0; i < count; i++) {
      if (isInvalidHextet(parts[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks all the parts are valid for {@code parts.length - count <= i < parts.length}.
   *
   * @param parts the parts
   * @param count the count
   * @return true if valid
   */
  private static boolean isValidCountDown(final String[] parts, int count) {
    for (int i = count; i > 0; i--) {
      if (isInvalidHextet(parts[parts.length - i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the the hextet is above the maximum value for an unsigned short (0xFFFF).
   *
   * @param ipPart the ip part
   * @return True if invalid
   */
  private static boolean isInvalidHextet(String ipPart) {
    // Note: we already verified that this string contains only hex digits.
    try {
      final int hextet = Integer.parseInt(ipPart, BASE_16);
      return hextet > MAX_UNSIGNED_SHORT;
    } catch (final NumberFormatException ex) {
      // Invalid
      return true;
    }
  }

  /**
   * Checks if the IP address is a valid IPv4 address:
   *
   * <pre>
   * 0-255 . 0-255 . 0-255 . 0-255
   * </pre>
   *
   * @param ipAddress the ip address
   * @return true if valid
   */
  public static boolean isIpV4(String ipAddress) {
    return IPV4_PATTERN.matcher(ipAddress).matches();
  }
}
