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

/**
 * Defines parameters for the Google Analytics Measurement Protocol.
 * 
 * <p>Parameters are expected to be {@code name=value} pairs.
 * 
 * @see <a href= "http://goo.gl/a8d4RP">Measurement Protocol Parameter Reference</a>
 */
public enum Parameter {
  //@formatter:off
  /** @see <a href= "http://goo.gl/a8d4RP#v">Protocol Version</a> */
  PROTOCOL_VERSION("Protocol Version", "v", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ht">Cache Buster</a> */
  CACHE_BUSTER("Cache Buster", "z", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cid">Client ID</a> */
  CLIENT_ID("Client ID", "cid", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cd_">Custom Dimension</a> */
  CUSTOM_DIMENSION("Custom Dimension", "cd_", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cd_">Custom Dimension</a> */
  CUSTOM_METRIC("Custom Metric", "cm_", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cid">Tracking ID</a> */
  TRACKING_ID("Tracking ID", "tid", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ht">Hit Type</a> */
  HIT_TYPE("Hit Type", "ht", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#qt">Queue Time</a> */
  QUEUE_TIME("Queue Time", "qt", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ul">User Language</a> */
  USER_LANGUAGE("User Language", "ul", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#sc">Session Control</a> */
  SESSION_CONTROL("Session Control", "sc", ValueType.TEXT, 0),
  //@formatter:on
  ;

  /**
   * The character used to identify an index within the name format for the {@code name=value}
   * parameter pair.
   * 
   * <p>package scope to allow other classes to use the same value. 
   */
  static final char INDEX_CHARACTER = '_';

  /** The name. */
  private final String name;

  /** The name format for the name part of the {@code name=value} pair. */
  private final char[] nameFormat;

  /** The number of indices. */
  private final int numberOfIndexes;

  /** The value type. */
  private final ValueType valueType;

  /**
   * The max length of the text.
   * 
   * <p>This applies {@code value} part of the parameter {@code name=value} pair.
   */
  private final int maxLength;

  /**
   * The supported hit types.
   * 
   * <p>If null then all types are supported
   */
  private final HitType[] supportedHitTypes;

  /**
   * Instantiates a new hit type.
   *
   * @param name the name
   * @param nameFormat the name format
   * @param valueType the value type
   * @param maxLength the max length
   * @param supportedHitTypes the supported hit types
   */
  private Parameter(String name, String nameFormat, ValueType valueType, int maxLength,
      HitType... supportedHitTypes) {
    this.name = name;
    this.nameFormat = nameFormat.toCharArray();
    this.valueType = valueType;
    this.maxLength = maxLength;
    this.supportedHitTypes = supportedHitTypes;
    this.numberOfIndexes = countIndexes(nameFormat);
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * WARNING: Package level direct access to the name format character array. This should not be modified!
   * 
   * @return the name format
   * @see <a href= "http://goo.gl/a8d4RP#pr_cc">Product Coupon Code</a>
   * @see #getNumberOfIndexes()
   */
  char[] getNameFormat() {
    // TODO - Build the code so that it is appropriately signed and cannot be added to by
    // externals 
    return nameFormat;
  }

  /**
   * Gets the format for the {@code name} part of the parameter {@code name=value} pair.
   * 
   * <p>This can contain zero of more indexes. Indexes will be identified with the {@code _}
   * character, for example {@code pr_cc} for Product Coupon Code where the {@code _} represents the
   * product index.
   * 
   * @return the name format
   * @see <a href= "http://goo.gl/a8d4RP#pr_cc">Product Coupon Code</a>
   * @see #getNumberOfIndexes()
   */
  public String getNameFormat2() {
    return new String(nameFormat);
  }
  
  /**
   * Gets the number of indexes.
   * 
   * <p>This is a count of the number of {@code _} (underscore) characters within the name format.
   *
   * @return the number of indexes
   * @see Parameter#getNameFormat()
   */
  public int getNumberOfIndexes() {
    return numberOfIndexes;
  }

  /**
   * Gets the type used for the parameter value.
   *
   * @return the value type
   */
  public ValueType getValueType() {
    return valueType;
  }

  /**
   * Gets the max length for the parameter value.
   *
   * <p>It is only relevant for {@link ValueType#TEXT} parameters.
   * 
   * <p>If zero then any length is allowed.
   * 
   * <p>Otherwise this specifies the maximum bytes for the {@code value} part of the parameter
   * {@code name=value} pair.
   * 
   * @return the max length
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * Gets the supported hit types for the parameter.
   *
   * <p>If null then all types are supported. Otherwise a clone of the supported types is returned.
   * 
   * @return the supported hit types
   * @see #isSupported(HitType)
   */
  public HitType[] getSupportedHitTypes() {
    return (supportedHitTypes == null) ? null : supportedHitTypes.clone();
  }

  /**
   * Checks the hit type is supported.
   * 
   * <p>If the argument is {@code null} then this will return {@code false}.
   * 
   * @param hitType the hit type
   * @return true, if is supported
   */
  public boolean isSupported(HitType hitType) {
    if (supportedHitTypes == null) {
      // Don't support the null hit type!
      return hitType != null;
    }
    // This assumes that supported hit types will never contain a null
    for (HitType supported : supportedHitTypes) {
      if (supported == hitType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Count the number of indexes in the name format.
   * 
   * <p>Indexes use the {@code _} (underscore) character.
   * 
   * <p>This assumes that all the characters are below
   * {@link Character#MIN_SUPPLEMENTARY_CODE_POINT}, i.e. this is not a string with supplementary
   * characters.
   *
   * @param nameFormat the name format
   * @return the count
   */
  public static int countIndexes(String nameFormat) {
    int count = 0;
    if (nameFormat != null) {
      // For faster access to all the chars get a copy avoiding String.charAt(int)
      char[] chars = nameFormat.toCharArray();
      for (int i = chars.length; i-- > 0;) {
        if (chars[i] == INDEX_CHARACTER) {
          count++;
        }
      }
    }
    return count;
  }
}
