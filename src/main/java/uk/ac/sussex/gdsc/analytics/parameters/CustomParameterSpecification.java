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

import java.util.EnumSet;
import java.util.Objects;

/**
 * Contains a custom parameter specification for the Google Analytics Measurement Protocol.
 */
public class CustomParameterSpecification implements ParameterSpecification {

  /** The formal name. */
  private final String formalName;

  /**
   * The formalName format for the formalName part of the {@code formalName=value} pair.
   */
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
   * <p>This is not null.
   */
  private final EnumSet<HitType> supportedHitTypes;

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
    this.formalName = Objects.requireNonNull(formalName, "Formal name");
    this.nameFormat = Objects.requireNonNull(nameFormat, "Name format");
    this.valueType = Objects.requireNonNull(valueType, "Value type");
    this.maxLength = maxLength;
    if (supportedHitTypes.length == 0) {
      this.supportedHitTypes = Constants.ALL_OF_HIT_TYPE;
    } else {
      // Note: This adds the first type again from the array argument but the set is built correctly
      this.supportedHitTypes = EnumSet.of(supportedHitTypes[0], supportedHitTypes);
    }
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
  public EnumSet<HitType> getSupportedHitTypes() {
    return supportedHitTypes.clone();
  }
}
