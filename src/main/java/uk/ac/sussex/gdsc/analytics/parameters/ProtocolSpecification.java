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

/**
 * Defines parameters for the Google Analytics Measurement Protocol.
 *
 * <p>Parameters are expected to be {@code formalName=value} pairs.
 *
 * @see <a href= "http://goo.gl/a8d4RP">Measurement Protocol Parameter Reference</a>
 */
public enum ProtocolSpecification implements ParameterSpecification {
  /////////////////////////
  // General
  /////////////////////////
  /**
   * Protocol Version (v).
   *
   * <p>Required for all hit types.
   *
   * <p>The Protocol version. The current value is &#39;1&#39;. This will only change when there are
   * changes made that are not backwards compatible.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: v=1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#v">Protocol Version</a>
   */
  PROTOCOL_VERSION("Protocol Version", "v", ValueType.TEXT, 0),
  /**
   * Tracking ID / Web Property ID (tid).
   *
   * <p>Required for all hit types.
   *
   * <p>The tracking ID / web property ID. The format is UA-XXXX-Y. All collected data is associated
   * by this ID.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: tid=UA-XXXX-Y.
   *
   * @see <a href= "http://goo.gl/a8d4RP#tid">Tracking ID / Web Property ID</a>
   */
  TRACKING_ID("Tracking ID / Web Property ID", "tid", ValueType.TEXT, 0),
  /**
   * Anonymize IP (aip).
   *
   * <p>Optional.
   *
   * <p>When present, the IP address of the sender will be anonymized. For example, the IP will be
   * anonymized if any of the following parameters are present in the payload: &amp;aip=,
   * &amp;aip=0, or &amp;aip=1.
   *
   * <p>Value Type: boolean.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: aip=1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#aip">Anonymize IP</a>
   */
  ANONYMIZE_IP("Anonymize IP", "aip", ValueType.BOOLEAN, 0),
  /**
   * Data Source (ds).
   *
   * <p>Optional.
   *
   * <p>Indicates the data source of the hit. Hits sent from analytics.js will have data source set
   * to &#39;web&#39;; hits sent from one of the mobile SDKs will have data source set to
   * &#39;app&#39;.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: ds=crm.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ds">Data Source</a>
   */
  DATA_SOURCE("Data Source", "ds", ValueType.TEXT, 0),
  /**
   * Queue Time (qt).
   *
   * <p>Optional.
   *
   * <p>Used to collect offline / latent hits. The value represents the time delta (in milliseconds)
   * between when the hit being reported occurred and the time the hit was sent. The value must be
   * greater than or equal to 0. Values greater than four hours may lead to hits not being
   * processed.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: qt=560.
   *
   * @see <a href= "http://goo.gl/a8d4RP#qt">Queue Time</a>
   */
  QUEUE_TIME("Queue Time", "qt", ValueType.INTEGER, 0),
  /**
   * Cache Buster (z).
   *
   * <p>Optional.
   *
   * <p>Used to send a random number in GET requests to ensure browsers and proxies don&#39;t cache
   * hits. It should be sent as the final parameter of the request since we&#39;ve seen some 3rd
   * party internet filtering software add additional parameters to HTTP requests incorrectly. This
   * value is not used in reporting.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: z=289372387623.
   *
   * @see <a href= "http://goo.gl/a8d4RP#z">Cache Buster</a>
   */
  CACHE_BUSTER("Cache Buster", "z", ValueType.TEXT, 0),

  /////////////////////////
  // User
  /////////////////////////
  /**
   * Client ID (cid).
   *
   * <p>Optional.
   *
   * <p>This field is required if User ID (uid) is not specified in the request. This anonymously
   * identifies a particular user, device, or browser instance. For the web, this is generally
   * stored as a first-party cookie with a two-year expiration. For mobile apps, this is randomly
   * generated for each particular instance of an application install. The value of this field
   * should be a random UUID (version 4) as described in http://www.ietf.org/rfc/rfc4122.txt.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cid=35009a79-1a05-49d7-b876-2b884d0f825b.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cid">Client ID</a>
   */
  CLIENT_ID("Client ID", "cid", ValueType.TEXT, 0),
  /**
   * User ID (uid).
   *
   * <p>Optional.
   *
   * <p>This field is required if Client ID (cid) is not specified in the request. This is intended
   * to be a known identifier for a user provided by the site owner/tracking library user. It must
   * not itself be PII (personally identifiable information). The value should never be persisted in
   * GA cookies or other Analytics provided storage.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: uid=as8eknlll.
   *
   * @see <a href= "http://goo.gl/a8d4RP#uid">User ID</a>
   */
  USER_ID("User ID", "uid", ValueType.TEXT, 0),

  /////////////////////////
  // Session
  /////////////////////////
  /**
   * Session Control (sc).
   *
   * <p>Optional.
   *
   * <p>Used to control the session duration. A value of &#39;start&#39; forces a new session to
   * start with this hit and &#39;end&#39; forces the current session to end with this hit. All
   * other values are ignored.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: sc=end.
   *
   * @see <a href= "http://goo.gl/a8d4RP#sc">Session Control</a>
   */
  SESSION_CONTROL("Session Control", "sc", ValueType.TEXT, 0),
  /**
   * IP Override (uip).
   *
   * <p>Optional.
   *
   * <p>The IP address of the user. This should be a valid IP address in IPv4 or IPv6 format. It
   * will always be anonymized just as though &amp;aip (anonymize IP) had been used.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: uip=1.2.3.4.
   *
   * @see <a href= "http://goo.gl/a8d4RP#uip">IP Override</a>
   */
  IP_OVERRIDE("IP Override", "uip", ValueType.TEXT, 0),
  /**
   * User Agent Override (ua).
   *
   * <p>Optional.
   *
   * <p>The User Agent of the browser. Note that Google has libraries to identify real user agents.
   * Hand crafting your own agent could break at any time.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage:
   * ua=Opera%2F9.80%20%28Windows%20NT%206.0%29%20Presto%2F2.12.388%20Version%2F12.14.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ua">User Agent Override</a>
   */
  USER_AGENT_OVERRIDE("User Agent Override", "ua", ValueType.TEXT, 0),
  /**
   * Geographical Override (geoid).
   *
   * <p>Optional.
   *
   * <p>The geographical location of the user. The geographical ID should be a two letter country
   * code or a criteria ID representing a city or region (see
   * http://developers.google.com/analytics/devguides/collection/protocol/v1/geoid). This parameter
   * takes precedent over any location derived from IP address, including the IP Override parameter.
   * An invalid code will result in geographical dimensions to be set to &#39;(not set)&#39;.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: geoid=21137.
   *
   * @see <a href= "http://goo.gl/a8d4RP#geoid">Geographical Override</a>
   */
  GEOGRAPHICAL_OVERRIDE("Geographical Override", "geoid", ValueType.TEXT, 0),

  /////////////////////////
  // Traffic Sources
  /////////////////////////
  /**
   * Document Referrer (dr).
   *
   * <p>Optional.
   *
   * <p>Specifies which referral source brought traffic to a website. This value is also used to
   * compute the traffic source. The format of this value is a URL.
   *
   * <p>Value Type: text (Max Length = 2048).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dr=http%3A%2F%2Fexample.com.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dr">Document Referrer</a>
   */
  DOCUMENT_REFERRER("Document Referrer", "dr", ValueType.TEXT, 2048),
  /**
   * Campaign Name (cn).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign name.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cn=%28direct%29.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cn">Campaign Name</a>
   */
  CAMPAIGN_NAME("Campaign Name", "cn", ValueType.TEXT, 100),
  /**
   * Campaign Source (cs).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign source.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cs=%28direct%29.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cs">Campaign Source</a>
   */
  CAMPAIGN_SOURCE("Campaign Source", "cs", ValueType.TEXT, 100),
  /**
   * Campaign Medium (cm).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign medium.
   *
   * <p>Value Type: text (Max Length = 50).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cm=organic.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cm">Campaign Medium</a>
   */
  CAMPAIGN_MEDIUM("Campaign Medium", "cm", ValueType.TEXT, 50),
  /**
   * Campaign Keyword (ck).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign keyword.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: ck=Blue%20Shoes.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ck">Campaign Keyword</a>
   */
  CAMPAIGN_KEYWORD("Campaign Keyword", "ck", ValueType.TEXT, 500),
  /**
   * Campaign Content (cc).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign content.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cc=content.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cc">Campaign Content</a>
   */
  CAMPAIGN_CONTENT("Campaign Content", "cc", ValueType.TEXT, 500),
  /**
   * Campaign ID (ci).
   *
   * <p>Optional.
   *
   * <p>Specifies the campaign ID.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: ci=ID.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ci">Campaign ID</a>
   */
  CAMPAIGN_ID("Campaign ID", "ci", ValueType.TEXT, 100),
  /**
   * Google Ads ID (gclid).
   *
   * <p>Optional.
   *
   * <p>Specifies the Google Ads Id.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: gclid=CL6Q-OXyqKUCFcgK2goddQuoHg.
   *
   * @see <a href= "http://goo.gl/a8d4RP#gclid">Google Ads ID</a>
   */
  GOOGLE_ADS_ID("Google Ads ID", "gclid", ValueType.TEXT, 0),
  /**
   * Google Display Ads ID (dclid).
   *
   * <p>Optional.
   *
   * <p>Specifies the Google Display Ads Id.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dclid=d_click_id.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dclid">Google Display Ads ID</a>
   */
  GOOGLE_DISPLAY_ADS_ID("Google Display Ads ID", "dclid", ValueType.TEXT, 0),

  /////////////////////////
  // System Info
  /////////////////////////
  /**
   * Screen Resolution (sr).
   *
   * <p>Optional.
   *
   * <p>Specifies the screen resolution.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: sr=800x600.
   *
   * @see <a href= "http://goo.gl/a8d4RP#sr">Screen Resolution</a>
   */
  SCREEN_RESOLUTION("Screen Resolution", "sr", ValueType.TEXT, 20),
  /**
   * Viewport size (vp).
   *
   * <p>Optional.
   *
   * <p>Specifies the viewable area of the browser / device.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: vp=123x456.
   *
   * @see <a href= "http://goo.gl/a8d4RP#vp">Viewport size</a>
   */
  VIEWPORT_SIZE("Viewport size", "vp", ValueType.TEXT, 20),
  /**
   * Document Encoding (de).
   *
   * <p>Optional.
   *
   * <p>Specifies the character set used to encode the page / document.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: de=UTF-8.
   *
   * @see <a href= "http://goo.gl/a8d4RP#de">Document Encoding</a>
   */
  DOCUMENT_ENCODING("Document Encoding", "de", ValueType.TEXT, 20),
  /**
   * Screen Colors (sd).
   *
   * <p>Optional.
   *
   * <p>Specifies the screen color depth.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: sd=24-bits.
   *
   * @see <a href= "http://goo.gl/a8d4RP#sd">Screen Colors</a>
   */
  SCREEN_COLORS("Screen Colors", "sd", ValueType.TEXT, 20),
  /**
   * User Language (ul).
   *
   * <p>Optional.
   *
   * <p>Specifies the language.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: ul=en-us.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ul">User Language</a>
   */
  USER_LANGUAGE("User Language", "ul", ValueType.TEXT, 20),
  /**
   * Java Enabled (je).
   *
   * <p>Optional.
   *
   * <p>Specifies whether Java was enabled.
   *
   * <p>Value Type: boolean.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: je=1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#je">Java Enabled</a>
   */
  JAVA_ENABLED("Java Enabled", "je", ValueType.BOOLEAN, 0),
  /**
   * Flash Version (fl).
   *
   * <p>Optional.
   *
   * <p>Specifies the flash version.
   *
   * <p>Value Type: text (Max Length = 20).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: fl=10%201%20r103.
   *
   * @see <a href= "http://goo.gl/a8d4RP#fl">Flash Version</a>
   */
  FLASH_VERSION("Flash Version", "fl", ValueType.TEXT, 20),

  /////////////////////////
  // Hit
  /////////////////////////
  /**
   * Hit type (t).
   *
   * <p>Required for all hit types.
   *
   * <p>The type of hit. Must be one of &#39;pageview&#39;, &#39;screenview&#39;, &#39;event&#39;,
   * &#39;transaction&#39;, &#39;item&#39;, &#39;social&#39;, &#39;exception&#39;, &#39;timing&#39;.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: t=pageview.
   *
   * @see <a href= "http://goo.gl/a8d4RP#t">Hit type</a>
   */
  HIT_TYPE("Hit type", "t", ValueType.TEXT, 0),
  /**
   * Non-Interaction Hit (ni).
   *
   * <p>Optional.
   *
   * <p>Specifies that a hit be considered non-interactive.
   *
   * <p>Value Type: boolean.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: ni=1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ni">Non-Interaction Hit</a>
   */
  NON_INTERACTION_HIT("Non-Interaction Hit", "ni", ValueType.BOOLEAN, 0),

  /////////////////////////
  // Content Information
  /////////////////////////
  /**
   * Document location URL (dl).
   *
   * <p>Optional.
   *
   * <p>Use this parameter to send the full URL (document location) of the page on which content
   * resides. You can use the &amp;dh and &amp;dp parameters to override the hostname and path +
   * query portions of the document location, accordingly. The JavaScript clients determine this
   * parameter using the concatenation of the document.location.origin + document.location.pathname
   * + document.location.search browser parameters. Be sure to remove any user authentication or
   * other private information from the URL if present. For &#39;pageview&#39; hits, either &amp;dl
   * or both &amp;dh and &amp;dp have to be specified for the hit to be valid.
   *
   * <p>Value Type: text (Max Length = 2048).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dl=http%3A%2F%2Ffoo.com%2Fhome%3Fa%3Db.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dl">Document location URL</a>
   */
  DOCUMENT_LOCATION_URL("Document location URL", "dl", ValueType.TEXT, 2048),
  /**
   * Document Host Name (dh).
   *
   * <p>Optional.
   *
   * <p>Specifies the hostname from which content was hosted.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dh=foo.com.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dh">Document Host Name</a>
   */
  DOCUMENT_HOST_NAME("Document Host Name", "dh", ValueType.TEXT, 100),
  /**
   * Document Path (dp).
   *
   * <p>Optional.
   *
   * <p>The path portion of the page URL. Should begin with &#39;/&#39;. For &#39;pageview&#39;
   * hits, either &amp;dl or both &amp;dh and &amp;dp have to be specified for the hit to be valid.
   *
   * <p>Value Type: text (Max Length = 2048).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dp=%2Ffoo.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dp">Document Path</a>
   */
  DOCUMENT_PATH("Document Path", "dp", ValueType.TEXT, 2048),
  /**
   * Document Title (dt).
   *
   * <p>Optional.
   *
   * <p>The title of the page / document.
   *
   * <p>Value Type: text (Max Length = 1500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: dt=Settings.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dt">Document Title</a>
   */
  DOCUMENT_TITLE("Document Title", "dt", ValueType.TEXT, 1500),
  /**
   * Screen Name (cd).
   *
   * <p>Required for screenview hit type.
   *
   * <p>This parameter is optional on web properties, and required on mobile properties for
   * screenview hits, where it is used for the &#39;Screen Name&#39; of the screenview hit. On web
   * properties this will default to the unique URL of the page by either using the &amp;dl
   * parameter as-is or assembling it from &amp;dh and &amp;dp.
   *
   * <p>Value Type: text (Max Length = 2048).
   *
   * <p>Supported Hit Types: screenview.
   *
   * <p>Example usage: cd=High%20Scores.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cd">Screen Name</a>
   */
  SCREEN_NAME("Screen Name", "cd", ValueType.TEXT, 2048, HitType.SCREENVIEW),
  /**
   * Content Group (cg_).
   *
   * <p>Optional.
   *
   * <p>You can have up to 5 content groupings, each of which has an associated index between 1 and
   * 5, inclusive. Each content grouping can have up to 100 content groups. The value of a content
   * group is hierarchical text delimited by &#39;/&#34;. All leading and trailing slashes will be
   * removed and any repeated slashes will be reduced to a single slash. For example,
   * &#39;/a//b/&#39; will be converted to &#39;a/b&#39;.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cg1=news%2Fsports.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cg_">Content Group</a>
   */
  CONTENT_GROUP("Content Group", "cg_", ValueType.TEXT, 100),
  /**
   * Link ID (linkid).
   *
   * <p>Optional.
   *
   * <p>The ID of a clicked DOM element, used to disambiguate multiple links to the same URL in
   * In-Page Analytics reports when Enhanced Link Attribution is enabled for the property.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: linkid=nav_bar.
   *
   * @see <a href= "http://goo.gl/a8d4RP#linkid">Link ID</a>
   */
  LINK_ID("Link ID", "linkid", ValueType.TEXT, 0),

  /////////////////////////
  // App Tracking
  /////////////////////////
  /**
   * Application Name (an).
   *
   * <p>Optional.
   *
   * <p>Specifies the application name. This field is required for any hit that has app related data
   * (i.e., app version, app ID, or app installer ID). For hits sent to web properties, this field
   * is optional.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: an=My%20App.
   *
   * @see <a href= "http://goo.gl/a8d4RP#an">Application Name</a>
   */
  APPLICATION_NAME("Application Name", "an", ValueType.TEXT, 100),
  /**
   * Application ID (aid).
   *
   * <p>Optional.
   *
   * <p>Application identifier.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: aid=com.company.app.
   *
   * @see <a href= "http://goo.gl/a8d4RP#aid">Application ID</a>
   */
  APPLICATION_ID("Application ID", "aid", ValueType.TEXT, 150),
  /**
   * Application Version (av).
   *
   * <p>Optional.
   *
   * <p>Specifies the application version.
   *
   * <p>Value Type: text (Max Length = 100).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: av=1.2.
   *
   * @see <a href= "http://goo.gl/a8d4RP#av">Application Version</a>
   */
  APPLICATION_VERSION("Application Version", "av", ValueType.TEXT, 100),
  /**
   * Application Installer ID (aiid).
   *
   * <p>Optional.
   *
   * <p>Application installer identifier.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: aiid=com.platform.vending.
   *
   * @see <a href= "http://goo.gl/a8d4RP#aiid">Application Installer ID</a>
   */
  APPLICATION_INSTALLER_ID("Application Installer ID", "aiid", ValueType.TEXT, 150),

  /////////////////////////
  // Event Tracking
  /////////////////////////
  /**
   * Event Category (ec).
   *
   * <p>Required for event hit type.
   *
   * <p>Specifies the event category. Must not be empty.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: event.
   *
   * <p>Example usage: ec=Category.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ec">Event Category</a>
   */
  EVENT_CATEGORY("Event Category", "ec", ValueType.TEXT, 150, HitType.EVENT),
  /**
   * Event Action (ea).
   *
   * <p>Required for event hit type.
   *
   * <p>Specifies the event action. Must not be empty.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: event.
   *
   * <p>Example usage: ea=Action.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ea">Event Action</a>
   */
  EVENT_ACTION("Event Action", "ea", ValueType.TEXT, 500, HitType.EVENT),
  /**
   * Event Label (el).
   *
   * <p>Optional.
   *
   * <p>Specifies the event label.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: event.
   *
   * <p>Example usage: el=Label.
   *
   * @see <a href= "http://goo.gl/a8d4RP#el">Event Label</a>
   */
  EVENT_LABEL("Event Label", "el", ValueType.TEXT, 500, HitType.EVENT),
  /**
   * Event Value (ev).
   *
   * <p>Optional.
   *
   * <p>Specifies the event value. Values must be non-negative.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: event.
   *
   * <p>Example usage: ev=55.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ev">Event Value</a>
   */
  EVENT_VALUE("Event Value", "ev", ValueType.INTEGER, 0, HitType.EVENT),

  /////////////////////////
  // E-Commerce
  /////////////////////////
  /**
   * Transaction ID (ti).
   *
   * <p>Required for item hit type.
   *
   * <p>A unique identifier for the transaction. This value should be the same for both the
   * Transaction hit and Items hits associated to the particular transaction.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: transaction, item.
   *
   * <p>Example usage: ti=OD564.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ti">Transaction ID</a>
   */
  TRANSACTION_ID("Transaction ID", "ti", ValueType.TEXT, 500, HitType.TRANSACTION, HitType.ITEM),
  /**
   * Transaction Affiliation (ta).
   *
   * <p>Optional.
   *
   * <p>Specifies the affiliation or store name.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: transaction.
   *
   * <p>Example usage: ta=Member.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ta">Transaction Affiliation</a>
   */
  TRANSACTION_AFFILIATION("Transaction Affiliation", "ta", ValueType.TEXT, 500,
      HitType.TRANSACTION),
  /**
   * Transaction Revenue (tr).
   *
   * <p>Optional.
   *
   * <p>Specifies the total revenue associated with the transaction. This value should include any
   * shipping or tax costs.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: transaction.
   *
   * <p>Example usage: tr=15.47.
   *
   * @see <a href= "http://goo.gl/a8d4RP#tr">Transaction Revenue</a>
   */
  TRANSACTION_REVENUE("Transaction Revenue", "tr", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /**
   * Transaction Shipping (ts).
   *
   * <p>Optional.
   *
   * <p>Specifies the total shipping cost of the transaction.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: transaction.
   *
   * <p>Example usage: ts=3.50.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ts">Transaction Shipping</a>
   */
  TRANSACTION_SHIPPING("Transaction Shipping", "ts", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /**
   * Transaction Tax (tt).
   *
   * <p>Optional.
   *
   * <p>Specifies the total tax of the transaction.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: transaction.
   *
   * <p>Example usage: tt=11.20.
   *
   * @see <a href= "http://goo.gl/a8d4RP#tt">Transaction Tax</a>
   */
  TRANSACTION_TAX("Transaction Tax", "tt", ValueType.CURRENCY, 0, HitType.TRANSACTION),
  /**
   * Item Name (in).
   *
   * <p>Required for item hit type.
   *
   * <p>Specifies the item name.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: item.
   *
   * <p>Example usage: in=Shoe.
   *
   * @see <a href= "http://goo.gl/a8d4RP#in">Item Name</a>
   */
  ITEM_NAME("Item Name", "in", ValueType.TEXT, 500, HitType.ITEM),
  /**
   * Item Price (ip).
   *
   * <p>Optional.
   *
   * <p>Specifies the price for a single item / unit.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: item.
   *
   * <p>Example usage: ip=3.50.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ip">Item Price</a>
   */
  ITEM_PRICE("Item Price", "ip", ValueType.CURRENCY, 0, HitType.ITEM),
  /**
   * Item Quantity (iq).
   *
   * <p>Optional.
   *
   * <p>Specifies the number of items purchased.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: item.
   *
   * <p>Example usage: iq=4.
   *
   * @see <a href= "http://goo.gl/a8d4RP#iq">Item Quantity</a>
   */
  ITEM_QUANTITY("Item Quantity", "iq", ValueType.INTEGER, 0, HitType.ITEM),
  /**
   * Item Code (ic).
   *
   * <p>Optional.
   *
   * <p>Specifies the SKU or item code.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: item.
   *
   * <p>Example usage: ic=SKU47.
   *
   * @see <a href= "http://goo.gl/a8d4RP#ic">Item Code</a>
   */
  ITEM_CODE("Item Code", "ic", ValueType.TEXT, 500, HitType.ITEM),
  /**
   * Item Category (iv).
   *
   * <p>Optional.
   *
   * <p>Specifies the category that the item belongs to.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: item.
   *
   * <p>Example usage: iv=Blue.
   *
   * @see <a href= "http://goo.gl/a8d4RP#iv">Item Category</a>
   */
  ITEM_CATEGORY("Item Category", "iv", ValueType.TEXT, 500, HitType.ITEM),

  /////////////////////////
  // Enhanced E-Commerce
  /////////////////////////
  /**
   * Product SKU (pr_id).
   *
   * <p>Optional.
   *
   * <p>The SKU of the product. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1id=P12345.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_id">Product SKU</a>
   */
  PRODUCT_SKU("Product SKU", "pr_id", ValueType.TEXT, 500),
  /**
   * Product Name (pr_nm).
   *
   * <p>Optional.
   *
   * <p>The name of the product. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1nm=Android%20T-Shirt.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_nm">Product Name</a>
   */
  PRODUCT_NAME("Product Name", "pr_nm", ValueType.TEXT, 500),
  /**
   * Product Brand (pr_br).
   *
   * <p>Optional.
   *
   * <p>The brand associated with the product. Product index must be a positive integer between 1
   * and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before
   * using this field.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1br=Google.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_br">Product Brand</a>
   */
  PRODUCT_BRAND("Product Brand", "pr_br", ValueType.TEXT, 500),
  /**
   * Product Category (pr_ca).
   *
   * <p>Optional.
   *
   * <p>The category to which the product belongs. Product index must be a positive integer between
   * 1 and 200, inclusive. The product category parameter can be hierarchical. Use / as a delimiter
   * to specify up to 5-levels of hierarchy. For analytics.js the Enhanced Ecommerce plugin must be
   * installed before using this field. .
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1ca=Apparel%2FMens%2FT-Shirts.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_ca">Product Category</a>
   */
  PRODUCT_CATEGORY("Product Category", "pr_ca", ValueType.TEXT, 500),
  /**
   * Product Variant (pr_va).
   *
   * <p>Optional.
   *
   * <p>The variant of the product. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1va=Black.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_va">Product Variant</a>
   */
  PRODUCT_VARIANT("Product Variant", "pr_va", ValueType.TEXT, 500),
  /**
   * Product Price (pr_pr).
   *
   * <p>Optional.
   *
   * <p>The unit price of a product. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1pr=29.20.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_pr">Product Price</a>
   */
  PRODUCT_PRICE("Product Price", "pr_pr", ValueType.CURRENCY, 0),
  /**
   * Product Quantity (pr_qt).
   *
   * <p>Optional.
   *
   * <p>The quantity of a product. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1qt=2.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_qt">Product Quantity</a>
   */
  PRODUCT_QUANTITY("Product Quantity", "pr_qt", ValueType.INTEGER, 0),
  /**
   * Product Coupon Code (pr_cc).
   *
   * <p>Optional.
   *
   * <p>The coupon code associated with a product. Product index must be a positive integer between
   * 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before
   * using this field.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1cc=SUMMER_SALE13.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_cc">Product Coupon Code</a>
   */
  PRODUCT_COUPON_CODE("Product Coupon Code", "pr_cc", ValueType.TEXT, 500),
  /**
   * Product Position (pr_ps).
   *
   * <p>Optional.
   *
   * <p>The product&#39;s position in a list or collection. Product index must be a positive integer
   * between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed
   * before using this field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1ps=2.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_ps">Product Position</a>
   */
  PRODUCT_POSITION("Product Position", "pr_ps", ValueType.INTEGER, 0),
  /**
   * Product Custom Dimension (pr_cd_).
   *
   * <p>Optional.
   *
   * <p>A product-level custom dimension where dimension index is a positive integer between 1 and
   * 200, inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1cd2=Member.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_cd_">Product Custom Dimension</a>
   */
  PRODUCT_CUSTOM_DIMENSION("Product Custom Dimension", "pr_cd_", ValueType.TEXT, 150),
  /**
   * Product Custom Metric (pr_cm_).
   *
   * <p>Optional.
   *
   * <p>A product-level custom metric where metric index is a positive integer between 1 and 200,
   * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pr1cm2=28.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pr_cm_">Product Custom Metric</a>
   */
  PRODUCT_CUSTOM_METRIC("Product Custom Metric", "pr_cm_", ValueType.INTEGER, 0),
  /**
   * Product Action (pa).
   *
   * <p>Optional.
   *
   * <p>The role of the products included in a hit. If a product action is not specified, all
   * product definitions included with the hit will be ignored. Must be one of: detail, click, add,
   * remove, checkout, checkout_option, purchase, refund. For analytics.js the Enhanced Ecommerce
   * plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pa=detail.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pa">Product Action</a>
   */
  PRODUCT_ACTION("Product Action", "pa", ValueType.TEXT, 0),
  /**
   * Coupon Code (tcc).
   *
   * <p>Optional.
   *
   * <p>The transaction coupon redeemed with the transaction. This is an additional parameter that
   * can be sent when Product Action is set to &#39;purchase&#39; or &#39;refund&#39;. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: tcc=SUMMER08.
   *
   * @see <a href= "http://goo.gl/a8d4RP#tcc">Coupon Code</a>
   */
  COUPON_CODE("Coupon Code", "tcc", ValueType.TEXT, 0),
  /**
   * Product Action List (pal).
   *
   * <p>Optional.
   *
   * <p>The list or collection from which a product action occurred. This is an additional parameter
   * that can be sent when Product Action is set to &#39;detail&#39; or &#39;click&#39;. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: pal=Search%20Results.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pal">Product Action List</a>
   */
  PRODUCT_ACTION_LIST("Product Action List", "pal", ValueType.TEXT, 0),
  /**
   * Checkout Step (cos).
   *
   * <p>Optional.
   *
   * <p>The step number in a checkout funnel. This is an additional parameter that can be sent when
   * Product Action is set to &#39;checkout&#39;. For analytics.js the Enhanced Ecommerce plugin
   * must be installed before using this field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cos=2.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cos">Checkout Step</a>
   */
  CHECKOUT_STEP("Checkout Step", "cos", ValueType.INTEGER, 0),
  /**
   * Checkout Step Option (col).
   *
   * <p>Optional.
   *
   * <p>Additional information about a checkout step. This is an additional parameter that can be
   * sent when Product Action is set to &#39;checkout&#39;. For analytics.js the Enhanced Ecommerce
   * plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: col=Visa.
   *
   * @see <a href= "http://goo.gl/a8d4RP#col">Checkout Step Option</a>
   */
  CHECKOUT_STEP_OPTION("Checkout Step Option", "col", ValueType.TEXT, 0),
  /**
   * Product Impression List Name (il_nm).
   *
   * <p>Optional.
   *
   * <p>The list or collection to which a product belongs. Impression List index must be a positive
   * integer between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be
   * installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1nm=Search%20Results.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_nm">Product Impression List Name</a>
   */
  PRODUCT_IMPRESSION_LIST_NAME("Product Impression List Name", "il_nm", ValueType.TEXT, 0),
  /**
   * Product Impression SKU (il_pi_id).
   *
   * <p>Optional.
   *
   * <p>The product ID or SKU. Impression List index must be a positive integer between 1 and 200,
   * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2id=P67890.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_id">Product Impression SKU</a>
   */
  PRODUCT_IMPRESSION_SKU("Product Impression SKU", "il_pi_id", ValueType.TEXT, 0),
  /**
   * Product Impression Name (il_pi_nm).
   *
   * <p>Optional.
   *
   * <p>The name of the product. Impression List index must be a positive integer between 1 and 200,
   * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2nm=Android%20T-Shirt.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_nm">Product Impression Name</a>
   */
  PRODUCT_IMPRESSION_NAME("Product Impression Name", "il_pi_nm", ValueType.TEXT, 0),
  /**
   * Product Impression Brand (il_pi_br).
   *
   * <p>Optional.
   *
   * <p>The brand associated with the product. Impression List index must be a positive integer
   * between 1 and 200, inclusive. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2br=Google.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_br">Product Impression Brand</a>
   */
  PRODUCT_IMPRESSION_BRAND("Product Impression Brand", "il_pi_br", ValueType.TEXT, 0),
  /**
   * Product Impression Category (il_pi_ca).
   *
   * <p>Optional.
   *
   * <p>The category to which the product belongs. Impression List index must be a positive integer
   * between 1 and 200, inclusive. Product index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2ca=Apparel.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_ca">Product Impression Category</a>
   */
  PRODUCT_IMPRESSION_CATEGORY("Product Impression Category", "il_pi_ca", ValueType.TEXT, 0),
  /**
   * Product Impression Variant (il_pi_va).
   *
   * <p>Optional.
   *
   * <p>The variant of the product. Impression List index must be a positive integer between 1 and
   * 200, inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2va=Black.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_va">Product Impression Variant</a>
   */
  PRODUCT_IMPRESSION_VARIANT("Product Impression Variant", "il_pi_va", ValueType.TEXT, 0),
  /**
   * Product Impression Position (il_pi_ps).
   *
   * <p>Optional.
   *
   * <p>The product&#39;s position in a list or collection. Impression List index must be a positive
   * integer between 1 and 200, inclusive. Product index must be a positive integer between 1 and
   * 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using
   * this field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2ps=2.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_ps">Product Impression Position</a>
   */
  PRODUCT_IMPRESSION_POSITION("Product Impression Position", "il_pi_ps", ValueType.INTEGER, 0),
  /**
   * Product Impression Price (il_pi_pr).
   *
   * <p>Optional.
   *
   * <p>The price of a product. Impression List index must be a positive integer between 1 and 200,
   * inclusive. Product index must be a positive integer between 1 and 200, inclusive. For
   * analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: currency.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2pr=29.20.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_pr">Product Impression Price</a>
   */
  PRODUCT_IMPRESSION_PRICE("Product Impression Price", "il_pi_pr", ValueType.CURRENCY, 0),
  /**
   * Product Impression Custom Dimension (il_pi_cd_).
   *
   * <p>Optional.
   *
   * <p>A product-level custom dimension where dimension index is a positive integer between 1 and
   * 200, inclusive. Impression List index must be a positive integer between 1 and 200, inclusive.
   * Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the
   * Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2cd3=Member.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_cd_">Product Impression Custom Dimension</a>
   */
  PRODUCT_IMPRESSION_CUSTOM_DIMENSION("Product Impression Custom Dimension", "il_pi_cd_",
      ValueType.TEXT, 0),
  /**
   * Product Impression Custom Metric (il_pi_cm_).
   *
   * <p>Optional.
   *
   * <p>A product-level custom metric where metric index is a positive integer between 1 and 200,
   * inclusive. Impression List index must be a positive integer between 1 and 200, inclusive.
   * Product index must be a positive integer between 1 and 200, inclusive. For analytics.js the
   * Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: il1pi2cm3=28.
   *
   * @see <a href= "http://goo.gl/a8d4RP#il_pi_cm_">Product Impression Custom Metric</a>
   */
  PRODUCT_IMPRESSION_CUSTOM_METRIC("Product Impression Custom Metric", "il_pi_cm_",
      ValueType.INTEGER, 0),
  /**
   * Promotion ID (promo_id).
   *
   * <p>Optional.
   *
   * <p>The promotion ID. Promotion index must be a positive integer between 1 and 200, inclusive.
   * For analytics.js the Enhanced Ecommerce plugin must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: promo1id=SHIP.
   *
   * @see <a href= "http://goo.gl/a8d4RP#promo_id">Promotion ID</a>
   */
  PROMOTION_ID("Promotion ID", "promo_id", ValueType.TEXT, 0),
  /**
   * Promotion Name (promo_nm).
   *
   * <p>Optional.
   *
   * <p>The name of the promotion. Promotion index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: promo1nm=Free%20Shipping.
   *
   * @see <a href= "http://goo.gl/a8d4RP#promo_nm">Promotion Name</a>
   */
  PROMOTION_NAME("Promotion Name", "promo_nm", ValueType.TEXT, 0),
  /**
   * Promotion Creative (promo_cr).
   *
   * <p>Optional.
   *
   * <p>The creative associated with the promotion. Promotion index must be a positive integer
   * between 1 and 200, inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed
   * before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: promo1cr=Shipping%20Banner.
   *
   * @see <a href= "http://goo.gl/a8d4RP#promo_cr">Promotion Creative</a>
   */
  PROMOTION_CREATIVE("Promotion Creative", "promo_cr", ValueType.TEXT, 0),
  /**
   * Promotion Position (promo_ps).
   *
   * <p>Optional.
   *
   * <p>The position of the creative. Promotion index must be a positive integer between 1 and 200,
   * inclusive. For analytics.js the Enhanced Ecommerce plugin must be installed before using this
   * field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: promo1ps=banner_slot_1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#promo_ps">Promotion Position</a>
   */
  PROMOTION_POSITION("Promotion Position", "promo_ps", ValueType.TEXT, 0),
  /**
   * Promotion Action (promoa).
   *
   * <p>Optional.
   *
   * <p>Specifies the role of the promotions included in a hit. If a promotion action is not
   * specified, the default promotion action, &#39;view&#39;, is assumed. To measure a user click on
   * a promotion set this to &#39;promo_click&#39;. For analytics.js the Enhanced Ecommerce plugin
   * must be installed before using this field.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: promoa=click.
   *
   * @see <a href= "http://goo.gl/a8d4RP#promoa">Promotion Action</a>
   */
  PROMOTION_ACTION("Promotion Action", "promoa", ValueType.TEXT, 0),
  /**
   * Currency Code (cu).
   *
   * <p>Optional.
   *
   * <p>When present indicates the local currency for all transaction currency values. Value should
   * be a valid ISO 4217 currency code.
   *
   * <p>Value Type: text (Max Length = 10).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cu=EUR.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cu">Currency Code</a>
   */
  CURRENCY_CODE("Currency Code", "cu", ValueType.TEXT, 10),

  /////////////////////////
  // Social Interactions
  /////////////////////////
  /**
   * Social Network (sn).
   *
   * <p>Required for social hit type.
   *
   * <p>Specifies the social network, for example Facebook or Google Plus.
   *
   * <p>Value Type: text (Max Length = 50).
   *
   * <p>Supported Hit Types: social.
   *
   * <p>Example usage: sn=facebook.
   *
   * @see <a href= "http://goo.gl/a8d4RP#sn">Social Network</a>
   */
  SOCIAL_NETWORK("Social Network", "sn", ValueType.TEXT, 50, HitType.SOCIAL),
  /**
   * Social Action (sa).
   *
   * <p>Required for social hit type.
   *
   * <p>Specifies the social interaction action. For example on Google Plus when a user clicks the
   * +1 button, the social action is &#39;plus&#39;.
   *
   * <p>Value Type: text (Max Length = 50).
   *
   * <p>Supported Hit Types: social.
   *
   * <p>Example usage: sa=like.
   *
   * @see <a href= "http://goo.gl/a8d4RP#sa">Social Action</a>
   */
  SOCIAL_ACTION("Social Action", "sa", ValueType.TEXT, 50, HitType.SOCIAL),
  /**
   * Social Action Target (st).
   *
   * <p>Required for social hit type.
   *
   * <p>Specifies the target of a social interaction. This value is typically a URL but can be any
   * text.
   *
   * <p>Value Type: text (Max Length = 2048).
   *
   * <p>Supported Hit Types: social.
   *
   * <p>Example usage: st=http%3A%2F%2Ffoo.com.
   *
   * @see <a href= "http://goo.gl/a8d4RP#st">Social Action Target</a>
   */
  SOCIAL_ACTION_TARGET("Social Action Target", "st", ValueType.TEXT, 2048, HitType.SOCIAL),

  /////////////////////////
  // Timing
  /////////////////////////
  /**
   * User timing category (utc).
   *
   * <p>Required for timing hit type.
   *
   * <p>Specifies the user timing category.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: utc=category.
   *
   * @see <a href= "http://goo.gl/a8d4RP#utc">User timing category</a>
   */
  USER_TIMING_CATEGORY("User timing category", "utc", ValueType.TEXT, 150, HitType.TIMING),
  /**
   * User timing variable name (utv).
   *
   * <p>Required for timing hit type.
   *
   * <p>Specifies the user timing variable.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: utv=lookup.
   *
   * @see <a href= "http://goo.gl/a8d4RP#utv">User timing variable name</a>
   */
  USER_TIMING_VARIABLE_NAME("User timing variable name", "utv", ValueType.TEXT, 500,
      HitType.TIMING),
  /**
   * User timing time (utt).
   *
   * <p>Required for timing hit type.
   *
   * <p>Specifies the user timing value. The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: utt=123.
   *
   * @see <a href= "http://goo.gl/a8d4RP#utt">User timing time</a>
   */
  USER_TIMING_TIME("User timing time", "utt", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * User timing label (utl).
   *
   * <p>Optional.
   *
   * <p>Specifies the user timing label.
   *
   * <p>Value Type: text (Max Length = 500).
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: utl=label.
   *
   * @see <a href= "http://goo.gl/a8d4RP#utl">User timing label</a>
   */
  USER_TIMING_LABEL("User timing label", "utl", ValueType.TEXT, 500, HitType.TIMING),
  /**
   * Page Load Time (plt).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for a page to load. The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: plt=3554.
   *
   * @see <a href= "http://goo.gl/a8d4RP#plt">Page Load Time</a>
   */
  PAGE_LOAD_TIME("Page Load Time", "plt", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * DNS Time (dns).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took to do a DNS lookup.The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: dns=43.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dns">DNS Time</a>
   */
  DNS_TIME("DNS Time", "dns", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * Page Download Time (pdt).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for the page to be downloaded. The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: pdt=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#pdt">Page Download Time</a>
   */
  PAGE_DOWNLOAD_TIME("Page Download Time", "pdt", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * Redirect Response Time (rrt).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for any redirects to happen. The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: rrt=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#rrt">Redirect Response Time</a>
   */
  REDIRECT_RESPONSE_TIME("Redirect Response Time", "rrt", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * TCP Connect Time (tcp).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for a TCP connection to be made. The value is in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: tcp=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#tcp">TCP Connect Time</a>
   */
  TCP_CONNECT_TIME("TCP Connect Time", "tcp", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * Server Response Time (srt).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for the server to respond after the connect time. The value is in
   * milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: srt=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#srt">Server Response Time</a>
   */
  SERVER_RESPONSE_TIME("Server Response Time", "srt", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * DOM Interactive Time (dit).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for Document.readyState to be &#39;interactive&#39;. The value is
   * in milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: dit=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#dit">DOM Interactive Time</a>
   */
  DOM_INTERACTIVE_TIME("DOM Interactive Time", "dit", ValueType.INTEGER, 0, HitType.TIMING),
  /**
   * Content Load Time (clt).
   *
   * <p>Optional.
   *
   * <p>Specifies the time it took for the DOMContentLoaded Event to fire. The value is in
   * milliseconds.
   *
   * <p>Value Type: integer.
   *
   * <p>Supported Hit Types: timing.
   *
   * <p>Example usage: clt=500.
   *
   * @see <a href= "http://goo.gl/a8d4RP#clt">Content Load Time</a>
   */
  CONTENT_LOAD_TIME("Content Load Time", "clt", ValueType.INTEGER, 0, HitType.TIMING),

  /////////////////////////
  // Exceptions
  /////////////////////////
  /**
   * Exception Description (exd).
   *
   * <p>Optional.
   *
   * <p>Specifies the description of an exception.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: exception.
   *
   * <p>Example usage: exd=DatabaseError.
   *
   * @see <a href= "http://goo.gl/a8d4RP#exd">Exception Description</a>
   */
  EXCEPTION_DESCRIPTION("Exception Description", "exd", ValueType.TEXT, 150, HitType.EXCEPTION),
  /**
   * Is Exception Fatal? (exf).
   *
   * <p>Optional.
   *
   * <p>Specifies whether the exception was fatal.
   *
   * <p>Value Type: boolean.
   *
   * <p>Supported Hit Types: exception.
   *
   * <p>Example usage: exf=0.
   *
   * @see <a href= "http://goo.gl/a8d4RP#exf">Is Exception Fatal?</a>
   */
  IS_EXCEPTION_FATAL("Is Exception Fatal?", "exf", ValueType.BOOLEAN, 0, HitType.EXCEPTION),

  /////////////////////////
  // Custom Dimensions / Metrics
  /////////////////////////
  /**
   * Custom Dimension (cd_).
   *
   * <p>Optional.
   *
   * <p>Each custom dimension has an associated index. There is a maximum of 20 custom dimensions
   * (200 for Analytics 360 accounts). The dimension index must be a positive integer between 1 and
   * 200, inclusive.
   *
   * <p>Value Type: text (Max Length = 150).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cd1=Sports.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cd_">Custom Dimension</a>
   */
  CUSTOM_DIMENSION("Custom Dimension", "cd_", ValueType.TEXT, 150),
  /**
   * Custom Metric (cm_).
   *
   * <p>Optional.
   *
   * <p>Each custom metric has an associated index. There is a maximum of 20 custom metrics (200 for
   * Analytics 360 accounts). The metric index must be a positive integer between 1 and 200,
   * inclusive.
   *
   * <p>Value Type: number.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: cm1=47.
   *
   * @see <a href= "http://goo.gl/a8d4RP#cm_">Custom Metric</a>
   */
  CUSTOM_METRIC("Custom Metric", "cm_", ValueType.NUMBER, 0),

  /////////////////////////
  // Content Experiments
  /////////////////////////
  /**
   * Experiment ID (xid).
   *
   * <p>Optional.
   *
   * <p>This parameter specifies that this user has been exposed to an experiment with the given ID.
   * It should be sent in conjunction with the Experiment Variant parameter.
   *
   * <p>Value Type: text (Max Length = 40).
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: xid=Qp0gahJ3RAO3DJ18b0XoUQ.
   *
   * @see <a href= "http://goo.gl/a8d4RP#xid">Experiment ID</a>
   */
  EXPERIMENT_ID("Experiment ID", "xid", ValueType.TEXT, 40),
  /**
   * Experiment Variant (xvar).
   *
   * <p>Optional.
   *
   * <p>Except as otherwise noted, the content of this page is licensed under the <a
   * href="https://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0
   * License</a>, and code samples are licensed under the <a
   * href="https://www.apache.org/licenses/LICENSE-2.0">Apache 2.0 License</a>. For details, see our
   * <a href="https://developers.google.com/terms/site-policies">Site Policies</a>. Java is a
   * registered trademark of Oracle and/or its affiliates.
   *
   * <p>Value Type: text.
   *
   * <p>Supported Hit Types: all.
   *
   * <p>Example usage: xvar=1.
   *
   * @see <a href= "http://goo.gl/a8d4RP#xvar">Experiment Variant</a>
   */
  EXPERIMENT_VARIANT("Experiment Variant", "xvar", ValueType.TEXT, 0),;

  /** The formal name. */
  private final String formalName;

  /**
   * The name format for the name part of the {@code name=value} pair.
   */
  private final String nameFormat;

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
  private final EnumSet<HitType> supportedHitTypes;

  /**
   * Creates a new instance.
   *
   * @param formalName the formal name
   * @param nameFormat the name format
   * @param valueType the value type
   * @param maxLength the max length
   * @param supportedHitTypes the supported hit types
   */
  ProtocolSpecification(String formalName, String nameFormat, ValueType valueType, int maxLength,
      HitType... supportedHitTypes) {
    this.formalName = formalName;
    this.nameFormat = nameFormat;
    this.valueType = valueType;
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
  public String getNameFormat() {
    // Note: The interface requires a generic CharSequence but this
    // explicitly returns a String
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

  /**
   * Gets the supported hit types for the parameter.
   *
   * <p>This method returns a copy of the set. The returned set is never {@code null}.
   */
  @Override
  public EnumSet<HitType> getSupportedHitTypes() {
    // This will not be null
    return supportedHitTypes.clone();
  }
}
