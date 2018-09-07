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
 * Contains a custom parameter specification for the Google Analytics Measurement Protocol.
 */
public class CustomParameterSpecification implements ParameterSpecification {

  /** The formal name. */
  private final String formalName;

  /** The formalName format for the formalName part of the {@code formalName=value} pair. */
  private final String nameFormat;

  /** The number of indices. */
  private final int numberOfIndexes;

  /** The value type. */
  private final ValueType valueType;

  /**
   * The max length of the text.
   * 
   * <p>This applies {@code value} part of the parameter {@code formalName=value} pair.
   */
  private final int maxLength;

  /**
   * The supported hit types.
   * 
   * <p>If null then all types are supported
   */
  private final HitType[] supportedHitTypes;

  /**
   * Instantiates a new custom parameter specification.
   *
   * @param formalName the formal name
   * @param nameFormat the name format
   * @param valueType the value type
   * @param maxLength the max length
   * @param supportedHitTypes the supported hit types
   */
  public CustomParameterSpecification(String formalName, String nameFormat, ValueType valueType,
      int maxLength, HitType... supportedHitTypes) {
    this.formalName = formalName;
    this.nameFormat = nameFormat;
    this.valueType = valueType;
    this.maxLength = maxLength;
    this.supportedHitTypes = supportedHitTypes;
    this.numberOfIndexes = ParameterUtils.countIndexes(nameFormat);
  }

  @Override
  public String getFormalName() {
    return formalName;
  }

  @Override
  public CharSequence getNameFormat() {
    return nameFormat;
  }

  @Override
  public int getNumberOfIndexes() {
    return numberOfIndexes;
  }

  @Override
  public ValueType getValueType() {
    return valueType;
  }

  @Override
  public int getMaxLength() {
    return maxLength;
  }

  @Override
  public HitType[] getSupportedHitTypes() {
    return (supportedHitTypes == null) ? null : supportedHitTypes.clone();
  }
}
