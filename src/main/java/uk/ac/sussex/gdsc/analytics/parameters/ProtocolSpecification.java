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
 * <p>Parameters are expected to be {@code formalName=value} pairs.
 * 
 * @see <a href= "http://goo.gl/a8d4RP">Measurement Protocol Parameter Reference</a>
 */
public enum ProtocolSpecification implements ParameterSpecification {
  // @formatter:off
  /////////////////////////
  // General
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#v">Protocol Version</a> */
  PROTOCOL_VERSION("Protocol Version", "v", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#tid">Tracking ID / Web Property ID</a> */
  TRACKING_ID("Tracking ID / Web Property ID", "tid", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#aip">Anonymize IP</a> */
  ANONYMIZE_IP("Anonymize IP", "aip", ValueType.BOOLEAN, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ds">Data Source</a> */
  DATA_SOURCE("Data Source", "ds", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#qt">Queue Time</a> */
  QUEUE_TIME("Queue Time", "qt", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#z">Cache Buster</a> */
  CACHE_BUSTER("Cache Buster", "z", ValueType.TEXT, 0),

  /////////////////////////
  // User
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#cid">Client ID</a> */
  CLIENT_ID("Client ID", "cid", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#uid">User ID</a> */
  USER_ID("User ID", "uid", ValueType.TEXT, 0),

  /////////////////////////
  // Session
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#sc">Session Control</a> */
  SESSION_CONTROL("Session Control", "sc", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#uip">IP Override</a> */
  IP_OVERRIDE("IP Override", "uip", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ua">User Agent Override</a> */
  USER_AGENT_OVERRIDE("User Agent Override", "ua", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#geoid">Geographical Override</a> */
  GEOGRAPHICAL_OVERRIDE("Geographical Override", "geoid", ValueType.TEXT, 0),

  /////////////////////////
  // Traffic Sources
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#dr">Document Referrer</a> */
  DOCUMENT_REFERRER("Document Referrer", "dr", ValueType.TEXT, 2048),
  /** @see <a href= "http://goo.gl/a8d4RP#cn">Campaign Name</a> */
  CAMPAIGN_NAME("Campaign Name", "cn", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#cs">Campaign Source</a> */
  CAMPAIGN_SOURCE("Campaign Source", "cs", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#cm">Campaign Medium</a> */
  CAMPAIGN_MEDIUM("Campaign Medium", "cm", ValueType.TEXT, 50),
  /** @see <a href= "http://goo.gl/a8d4RP#ck">Campaign Keyword</a> */
  CAMPAIGN_KEYWORD("Campaign Keyword", "ck", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#cc">Campaign Content</a> */
  CAMPAIGN_CONTENT("Campaign Content", "cc", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#ci">Campaign ID</a> */
  CAMPAIGN_ID("Campaign ID", "ci", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#gclid">Google Ads ID</a> */
  GOOGLE_ADS_ID("Google Ads ID", "gclid", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#dclid">Google Display Ads ID</a> */
  GOOGLE_DISPLAY_ADS_ID("Google Display Ads ID", "dclid", ValueType.TEXT, 0),

  /////////////////////////
  // System Info
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#sr">Screen Resolution</a> */
  SCREEN_RESOLUTION("Screen Resolution", "sr", ValueType.TEXT, 20),
  /** @see <a href= "http://goo.gl/a8d4RP#vp">Viewport size</a> */
  VIEWPORT_SIZE("Viewport size", "vp", ValueType.TEXT, 20),
  /** @see <a href= "http://goo.gl/a8d4RP#de">Document Encoding</a> */
  DOCUMENT_ENCODING("Document Encoding", "de", ValueType.TEXT, 20),
  /** @see <a href= "http://goo.gl/a8d4RP#sd">Screen Colors</a> */
  SCREEN_COLORS("Screen Colors", "sd", ValueType.TEXT, 20),
  /** @see <a href= "http://goo.gl/a8d4RP#ul">User Language</a> */
  USER_LANGUAGE("User Language", "ul", ValueType.TEXT, 20),
  /** @see <a href= "http://goo.gl/a8d4RP#je">Java Enabled</a> */
  JAVA_ENABLED("Java Enabled", "je", ValueType.BOOLEAN, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#fl">Flash Version</a> */
  FLASH_VERSION("Flash Version", "fl", ValueType.TEXT, 20),

  /////////////////////////
  // Hit
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#t">Hit type</a> */
  HIT_TYPE("Hit type", "t", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#ni">Non-Interaction Hit</a> */
  NON_INTERACTION_HIT("Non-Interaction Hit", "ni", ValueType.BOOLEAN, 0),

  /////////////////////////
  // Content Information
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#dl">Document location URL</a> */
  DOCUMENT_LOCATION_URL("Document location URL", "dl", ValueType.TEXT, 2048),
  /** @see <a href= "http://goo.gl/a8d4RP#dh">Document Host Name</a> */
  DOCUMENT_HOST_NAME("Document Host Name", "dh", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#dp">Document Path</a> */
  DOCUMENT_PATH("Document Path", "dp", ValueType.TEXT, 2048),
  /** @see <a href= "http://goo.gl/a8d4RP#dt">Document Title</a> */
  DOCUMENT_TITLE("Document Title", "dt", ValueType.TEXT, 1500),
  /** @see <a href= "http://goo.gl/a8d4RP#cd">Screen Name</a> */
  SCREEN_NAME("Screen Name", "cd", ValueType.TEXT, 2048, HitType.SCREENVIEW),
  /** @see <a href= "http://goo.gl/a8d4RP#cg_">Content Group</a> */
  CONTENT_GROUP("Content Group", "cg_", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#linkid">Link ID</a> */
  LINK_ID("Link ID", "linkid", ValueType.TEXT, 0),

  /////////////////////////
  // App Tracking
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#an">Application Name</a> */
  APPLICATION_NAME("Application Name", "an", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#aid">Application ID</a> */
  APPLICATION_ID("Application ID", "aid", ValueType.TEXT, 150),
  /** @see <a href= "http://goo.gl/a8d4RP#av">Application Version</a> */
  APPLICATION_VERSION("Application Version", "av", ValueType.TEXT, 100),
  /** @see <a href= "http://goo.gl/a8d4RP#aiid">Application Installer ID</a> */
  APPLICATION_INSTALLER_ID("Application Installer ID", "aiid", ValueType.TEXT, 150),

  /////////////////////////
  // Event Tracking
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#ec">Event Category</a> */
  EVENT_CATEGORY("Event Category", "ec", ValueType.TEXT, 150, HitType.EVENT),
  /** @see <a href= "http://goo.gl/a8d4RP#ea">Event Action</a> */
  EVENT_ACTION("Event Action", "ea", ValueType.TEXT, 500, HitType.EVENT),
  /** @see <a href= "http://goo.gl/a8d4RP#el">Event Label</a> */
  EVENT_LABEL("Event Label", "el", ValueType.TEXT, 500, HitType.EVENT),
  /** @see <a href= "http://goo.gl/a8d4RP#ev">Event Value</a> */
  EVENT_VALUE("Event Value", "ev", ValueType.INTEGER, 0, HitType.EVENT),

  /////////////////////////
  // E-Commerce
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#ti">Transaction ID</a> */
  TRANSACTION_ID("Transaction ID", "ti", ValueType.TEXT, 500, HitType.TRANSACTION, HitType.ITEM),
  /** @see <a href= "http://goo.gl/a8d4RP#ta">Transaction Affiliation</a> */
  TRANSACTION_AFFILIATION("Transaction Affiliation", "ta", ValueType.TEXT, 500, HitType.TRANSACTION),
  /** @see <a href= "http://goo.gl/a8d4RP#tr">Transaction Revenue</a> */
  TRANSACTION_REVENUE("Transaction Revenue", "tr", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /** @see <a href= "http://goo.gl/a8d4RP#ts">Transaction Shipping</a> */
  TRANSACTION_SHIPPING("Transaction Shipping", "ts", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /** @see <a href= "http://goo.gl/a8d4RP#tt">Transaction Tax</a> */
  TRANSACTION_TAX("Transaction Tax", "tt", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /** @see <a href= "http://goo.gl/a8d4RP#in">Item Name</a> */
  ITEM_NAME("Item Name", "in", ValueType.TEXT, 500, HitType.ITEM),
  /** @see <a href= "http://goo.gl/a8d4RP#ip">Item Price</a> */
  ITEM_PRICE("Item Price", "ip", ValueType.CURRENCY, 0, HitType.ITEM),
  /** @see <a href= "http://goo.gl/a8d4RP#iq">Item Quantity</a> */
  ITEM_QUANTITY("Item Quantity", "iq", ValueType.INTEGER, 0, HitType.ITEM),
  /** @see <a href= "http://goo.gl/a8d4RP#ic">Item Code</a> */
  ITEM_CODE("Item Code", "ic", ValueType.TEXT, 500, HitType.ITEM),
  /** @see <a href= "http://goo.gl/a8d4RP#iv">Item Category</a> */
  ITEM_CATEGORY("Item Category", "iv", ValueType.TEXT, 500, HitType.ITEM),

  /////////////////////////
  // Enhanced E-Commerce
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#pr_id">Product SKU</a> */
  PRODUCT_SKU("Product SKU", "pr_id", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_nm">Product Name</a> */
  PRODUCT_NAME("Product Name", "pr_nm", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_br">Product Brand</a> */
  PRODUCT_BRAND("Product Brand", "pr_br", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_ca">Product Category</a> */
  PRODUCT_CATEGORY("Product Category", "pr_ca", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_va">Product Variant</a> */
  PRODUCT_VARIANT("Product Variant", "pr_va", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_pr">Product Price</a> */
  PRODUCT_PRICE("Product Price", "pr_pr", ValueType.CURRENCY, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_qt">Product Quantity</a> */
  PRODUCT_QUANTITY("Product Quantity", "pr_qt", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_cc">Product Coupon Code</a> */
  PRODUCT_COUPON_CODE("Product Coupon Code", "pr_cc", ValueType.TEXT, 500),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_ps">Product Position</a> */
  PRODUCT_POSITION("Product Position", "pr_ps", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_cd_">Product Custom Dimension</a> */
  PRODUCT_CUSTOM_DIMENSION("Product Custom Dimension", "pr_cd_", ValueType.TEXT, 150),
  /** @see <a href= "http://goo.gl/a8d4RP#pr_cm_">Product Custom Metric</a> */
  PRODUCT_CUSTOM_METRIC("Product Custom Metric", "pr_cm_", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#pa">Product Action</a> */
  PRODUCT_ACTION("Product Action", "pa", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#tcc">Coupon Code</a> */
  COUPON_CODE("Coupon Code", "tcc", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#pal">Product Action List</a> */
  PRODUCT_ACTION_LIST("Product Action List", "pal", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cos">Checkout Step</a> */
  CHECKOUT_STEP("Checkout Step", "cos", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#col">Checkout Step Option</a> */
  CHECKOUT_STEP_OPTION("Checkout Step Option", "col", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_nm">Product Impression List Name</a> */
  PRODUCT_IMPRESSION_LIST_NAME("Product Impression List Name", "il_nm", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_id">Product Impression SKU</a> */
  PRODUCT_IMPRESSION_SKU("Product Impression SKU", "il_pi_id", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_nm">Product Impression Name</a> */
  PRODUCT_IMPRESSION_NAME("Product Impression Name", "il_pi_nm", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_br">Product Impression Brand</a> */
  PRODUCT_IMPRESSION_BRAND("Product Impression Brand", "il_pi_br", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_ca">Product Impression Category</a> */
  PRODUCT_IMPRESSION_CATEGORY("Product Impression Category", "il_pi_ca", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_va">Product Impression Variant</a> */
  PRODUCT_IMPRESSION_VARIANT("Product Impression Variant", "il_pi_va", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_ps">Product Impression Position</a> */
  PRODUCT_IMPRESSION_POSITION("Product Impression Position", "il_pi_ps", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_pr">Product Impression Price</a> */
  PRODUCT_IMPRESSION_PRICE("Product Impression Price", "il_pi_pr", ValueType.CURRENCY, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_cd_">Product Impression Custom Dimension</a> */
  PRODUCT_IMPRESSION_CUSTOM_DIMENSION("Product Impression Custom Dimension", "il_pi_cd_", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#il_pi_cm_">Product Impression Custom Metric</a> */
  PRODUCT_IMPRESSION_CUSTOM_METRIC("Product Impression Custom Metric", "il_pi_cm_", ValueType.INTEGER, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#promo_id">Promotion ID</a> */
  PROMOTION_ID("Promotion ID", "promo_id", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#promo_nm">Promotion Name</a> */
  PROMOTION_NAME("Promotion Name", "promo_nm", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#promo_cr">Promotion Creative</a> */
  PROMOTION_CREATIVE("Promotion Creative", "promo_cr", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#promo_ps">Promotion Position</a> */
  PROMOTION_POSITION("Promotion Position", "promo_ps", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#promoa">Promotion Action</a> */
  PROMOTION_ACTION("Promotion Action", "promoa", ValueType.TEXT, 0),
  /** @see <a href= "http://goo.gl/a8d4RP#cu">Currency Code</a> */
  CURRENCY_CODE("Currency Code", "cu", ValueType.TEXT, 10),

  /////////////////////////
  // Social Interactions
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#sn">Social Network</a> */
  SOCIAL_NETWORK("Social Network", "sn", ValueType.TEXT, 50, HitType.SOCIAL),
  /** @see <a href= "http://goo.gl/a8d4RP#sa">Social Action</a> */
  SOCIAL_ACTION("Social Action", "sa", ValueType.TEXT, 50, HitType.SOCIAL),
  /** @see <a href= "http://goo.gl/a8d4RP#st">Social Action Target</a> */
  SOCIAL_ACTION_TARGET("Social Action Target", "st", ValueType.TEXT, 2048, HitType.SOCIAL),

  /////////////////////////
  // Timing
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#utc">User timing category</a> */
  USER_TIMING_CATEGORY("User timing category", "utc", ValueType.TEXT, 150, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#utv">User timing variable name</a> */
  USER_TIMING_VARIABLE_NAME("User timing variable name", "utv", ValueType.TEXT, 500, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#utt">User timing time</a> */
  USER_TIMING_TIME("User timing time", "utt", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#utl">User timing label</a> */
  USER_TIMING_LABEL("User timing label", "utl", ValueType.TEXT, 500, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#plt">Page Load Time</a> */
  PAGE_LOAD_TIME("Page Load Time", "plt", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#dns">DNS Time</a> */
  DNS_TIME("DNS Time", "dns", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#pdt">Page Download Time</a> */
  PAGE_DOWNLOAD_TIME("Page Download Time", "pdt", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#rrt">Redirect Response Time</a> */
  REDIRECT_RESPONSE_TIME("Redirect Response Time", "rrt", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#tcp">TCP Connect Time</a> */
  TCP_CONNECT_TIME("TCP Connect Time", "tcp", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#srt">Server Response Time</a> */
  SERVER_RESPONSE_TIME("Server Response Time", "srt", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#dit">DOM Interactive Time</a> */
  DOM_INTERACTIVE_TIME("DOM Interactive Time", "dit", ValueType.INTEGER, 0, HitType.TIMING),
  /** @see <a href= "http://goo.gl/a8d4RP#clt">Content Load Time</a> */
  CONTENT_LOAD_TIME("Content Load Time", "clt", ValueType.INTEGER, 0, HitType.TIMING),

  /////////////////////////
  // Exceptions
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#exd">Exception Description</a> */
  EXCEPTION_DESCRIPTION("Exception Description", "exd", ValueType.TEXT, 150, HitType.EXCEPTION),
  /** @see <a href= "http://goo.gl/a8d4RP#exf">Is Exception Fatal?</a> */
  IS_EXCEPTION_FATAL("Is Exception Fatal?", "exf", ValueType.BOOLEAN, 0, HitType.EXCEPTION),

  /////////////////////////
  // Custom Dimensions / Metrics
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#cd_">Custom Dimension</a> */
  CUSTOM_DIMENSION("Custom Dimension", "cd_", ValueType.TEXT, 150),
  /** @see <a href= "http://goo.gl/a8d4RP#cm_">Custom Metric</a> */
  CUSTOM_METRIC("Custom Metric", "cm_", ValueType.NUMBER, 0),

  /////////////////////////
  // Content Experiments
  /////////////////////////
  /** @see <a href= "http://goo.gl/a8d4RP#xid">Experiment ID</a> */
  EXPERIMENT_ID("Experiment ID", "xid", ValueType.TEXT, 40),
  /** @see <a href= "http://goo.gl/a8d4RP#xvar">Experiment Variant</a> */
  EXPERIMENT_VARIANT("Experiment Variant", "xvar", ValueType.TEXT, 0),
  // @formatter:off
  ;

  /**
   * The character used to identify an index within the formalName format for the {@code formalName=value}
   * parameter pair.
   * 
   * <p>package scope to allow other classes to use the same value. 
   */
  static final char INDEX_CHARACTER = '_';

  /** The formal name. */
  private final String formalName;

  /** The formalName format for the formalName part of the {@code formalName=value} pair. */
  private final String nameFormat;

  /** 
   * A char array containing the formalName format string characters.
   * 
   * <p>This is used to provide faster access to the format at the package level.
   */
  private final char[] format;

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
   * Creates a new instance.
   *
   * @param formalName the formal name
   * @param nameFormat the name format
   * @param valueType the value type
   * @param maxLength the max length
   * @param supportedHitTypes the supported hit types
   */
  private ProtocolSpecification(String formalName, String nameFormat, ValueType valueType, int maxLength,
      HitType... supportedHitTypes) {
    this.formalName = formalName;
    this.format = nameFormat.toCharArray();
    this.nameFormat = nameFormat;
    this.valueType = valueType;
    this.maxLength = maxLength;
    this.supportedHitTypes = supportedHitTypes.clone();
    this.numberOfIndexes = ParameterUtils.countIndexes(nameFormat);
  }

  @Override
  public String getFormalName() {
    return formalName;
  }

  /**
   * WARNING: Package level direct access to the formalName format character array. This should not be modified!
   * 
   * @return the name format
   */
  char[] getNameFormatRef() {
    // TODO - Build the code so that it is appropriately signed and cannot be added to by
    // externals
    return format;
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
    // This will not be null
    return supportedHitTypes.clone();
  }
}
