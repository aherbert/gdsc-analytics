/*-
 * #%L
 * Genome Damage and Stability Centre Analytics Package
 *
 * The GDSC Analytics package contains code to use the Google Analytics
 * Measurement protocol to collect usage information from a Java application.
 * %%
 * Copyright (C) 2016 - 2018 Alex Herbert
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

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Contains Google Analytics Measurement Protocol parameters.
 *
 * <p>Note that although not enforced the set of {@code name=value} URL parameters should have
 * unique {@code name} tags. If not the hit may not be correctly processed by Google Analytics.
 *
 * @see <a href= "http://goo.gl/a8d4RP">Measurement Protocol Parameter Reference</a>
 */
public class Parameters implements FormattedParameter {

  /** The formatted parameters. */
  private final FormattedParameter[] formattedParameters;

  /**
   * Create a new instance.
   *
   * @param formattedParameters the formatted parameters
   */
  private Parameters(FormattedParameter[] formattedParameters) {
    // Note the input array is constructed by the builder using
    // private methods so it can be stored directly.
    this.formattedParameters = formattedParameters;
  }

  @Override
  public StringBuilder formatTo(StringBuilder sb) {
    if (formattedParameters.length != 0) {
      // The contract of formatTo is to assume the builder is empty.
      // It may not be (e.g. for batch hits (one on each line)) so get the size
      // and only add the '&' character when something has been added.
      final int size = sb.length();
      for (final FormattedParameter param : formattedParameters) {
        if (size != sb.length()) {
          sb.append(Constants.AND);
        }
        param.formatTo(sb);
      }
    }
    return sb;
  }

  /**
   * Create a new {@link RequiredBuilder} with the given Google Analytics tracking id.
   *
   * <p>The format is UA-XXXX-Y.
   *
   * <p>This is a specialist builder to ensure the version, tracking Id and client/user Id required
   * for all hits are included.
   *
   * @param trackingId the tracking id
   * @return the builder
   * @throws IllegalArgumentException if tracking ID is invalid
   * @see <a href="http://goo.gl/a8d4RP#tid">Tracking Id</a>
   */
  public static RequiredBuilder newRequiredBuilder(String trackingId) {
    return new RequiredBuilder(trackingId);
  }

  //@formatter:off
  /**
   * Create a new {@link PartialBuilder}.
   *
   * <p>This is a specialist builder to ensure the parameters required for each hit (version,
   * tracking Id; and client/user Id) are not duplicated when adding additional parameters.
   *
   * <p>The {@link PartialBuilder} provide support for using a typed parent to allow chaining:
   *
   * <pre>
   * // Simple parent builder
   * public class ParentBuilder {
   *
   *   private PartialBuilder&lt;ParentBuilder&gt; parameters;
   *
   *   public ParentBuilder() {
   *     parameters = Parameters.newPartialBuilder(this);
   *   }
   *
   *   public ParentBuilder doSomething() {
   *     // ... something useful
   *     return this;
   *   }
   *
   *   public PartialBuilder&lt;ParentBuilder&gt; getParameters() {
   *     return parameters;
   *   }
   *
   *   public Parameters build() {
   *     return parameters.build();
   *   }
   * }
   *
   * // Example chaining:
   * Parameters parameters = new ParentBuilder()
   *     .doSomething()
   *     .getParameters().addUserId("Mr.Demo")
   *                     .getParent()
   *     .build();
   * </pre>
   *
   * <p>The {@code parent} can be set to {@code null} if the {@link PartialBuilder#getParent()}
   * method will not be used.
   *
   * @param <P> the type of the parent
   * @param parent the parent
   * @return the builder
   */
  //@formatter:on
  public static <P> PartialBuilder<P> newPartialBuilder(P parent) {
    return new PartialBuilder<>(parent);
  }

  /**
   * Create a new {@link Builder}.
   *
   * <p>This is a generic builder with no defaults that exposes the parameter API. It can be used to
   * construct partial or complete hit parameters.
   *
   * @return the builder
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * A builder for {@link Parameters}.
   *
   * <p>Stores a typed-reference to the actual concrete builder class.
   *
   * <p>This implements the supported API but hides some methods as protected. Inheriting classes
   * are provided that expose the desired functionality.
   *
   * <p>Caution: The builder will not check for duplicate {@code name} tags. Thus is is possible to
   * misuse this builder to generate invalid URLs that have duplicates. The hit may not be correctly
   * processed by Google Analytics.
   *
   * @param <B> the [this] type of the class.
   * @see <a href="https://www.sitepoint.com/self-types-with-javas-generics/">Self Types with Java’s
   *      Generics</a>
   */
  public static class ParametersBuilder<B extends ParametersBuilder<B>> {

    /**
     * The initial list size.
     *
     * <p>This must not be zero otherwise doubling the size will fail.
     */
    private static final int INITIAL_LIST_SIZE = 8;

    /**
     * A reference to '{@code this}' cast to the appropriate {@code [this]} type.
     *
     * <p>This can be returned from any builder method, i.e. {@code return self; }
     */
    protected final B self;

    /**
     * The list.
     *
     * <p>This is implemented as a direct array for efficiency over using an ArrayList<> which has
     * fast-fail concurrent modification checks on all add operations.
     */
    private FormattedParameter[] list = new FormattedParameter[INITIAL_LIST_SIZE];

    /** The size of the list. */
    private int size;

    /**
     * Create a new builder.
     *
     * @param selfType the self type
     */
    @SuppressWarnings("unchecked")
    protected ParametersBuilder(Class<?> selfType) {
      self = (B) selfType.cast(this);
    }

    /**
     * Adds the parameter to the list.
     *
     * @param parameter the parameter
     * @return the builder
     */
    private B addParameter(FormattedParameter parameter) {
      if (list.length == size) {
        list = copyList(list, size, size * 2);
      }
      list[size++] = parameter;
      return self;
    }

    /**
     * Copy the list.
     *
     * @param list the list
     * @param size the size
     * @param capacity the capacity
     * @return the copy
     */
    private static FormattedParameter[] copyList(FormattedParameter[] list, int size,
        int capacity) {
      final FormattedParameter[] newList = new FormattedParameter[capacity];
      System.arraycopy(list, 0, newList, 0, size);
      return newList;
    }

    /**
     * Builds the {@link Parameters}.
     *
     * @return the parameters
     */
    public Parameters build() {
      return new Parameters(copyList(list, size, size));
    }

    /**
     * Adds the formatted parameter.
     *
     * @param parameter the parameter
     * @return the builder
     */
    public B add(FormattedParameter parameter) {
      return addParameter(Objects.requireNonNull(parameter, "Parameter is null"));
    }

    /**
     * Adds a generic {@code name=value} formatted parameter.
     *
     * <p>This is provided to add support for any of the protocol parameters that are not explicitly
     * provided.
     *
     * @param name the name
     * @param value the value
     * @return the builder
     * @throws IllegalArgumentException if the name is empty
     */
    public B add(String name, String value) {
      ParameterUtils.requireNotEmpty(name, "name is empty");
      return addParameter(new CustomParameter(name, value));
    }

    ////////////////////////////////////////////////////////
    // The following sections are in the order of the
    // Measurement Protocol Reference.
    // Not all the parameters are included.
    ////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    // General
    ////////////////////////////////////////////////////////

    /**
     * Adds the version.
     *
     * <p>Currently supports version 1.
     *
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#v">Protocol Version</a>
     */
    public B addVersion() {
      return addParameter(ProtocolVersionParameter.V1);
    }

    /**
     * Adds the tracking id.
     *
     * <p>The format is UA-XXXX-Y.
     *
     * @param trackingId the tracking id
     * @return the builder
     * @throws IllegalArgumentException if tracking ID is invalid
     * @see <a href="http://goo.gl/a8d4RP#tid">Tracking Id</a>
     */
    public B addTrackingId(String trackingId) {
      return addParameter(new TrackingIdParameter(trackingId));
    }

    /**
     * Adds the anonymize IP.
     *
     * <p>When present, the IP address of the sender will be anonymized.
     *
     * @param anonymizeIp the anonymize ip
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#aip">Anonymize IP</a>
     */
    public B addAnonymizeIp(boolean anonymizeIp) {
      return addParameter(
          new NoIndexBooleanParameter(ProtocolSpecification.ANONYMIZE_IP, anonymizeIp));
    }

    /**
     * Adds the data source.
     *
     * @param dataSource the data source
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#ds">Data Source</a>
     */
    public B addDataSource(String dataSource) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.DATA_SOURCE, dataSource));
    }

    /**
     * Adds the queue time using the provided timestamp (in milliseconds) to mark when the hit
     * occurred.
     *
     * <p>The queue time will be dynamically generated as the time delta (in milliseconds) between
     * when the hit being reported occurred and the time the hit was sent. The later time is when
     * the hit is created using {@link #build()}.
     *
     * @param timestamp the timestamp
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#qt">Queue Time</a>
     */
    public B addQueueTime(long timestamp) {
      return addParameter(new QueueTimeParameter(timestamp));
    }

    /**
     * Adds the queue time using the current time (in milliseconds) to mark when the hit occurred.
     *
     * <p>The queue time will be dynamically generated as the time delta (in milliseconds) between
     * when the hit being reported occurred and the time the hit was sent. The later time is when
     * the hit is created using {@link #build()}.
     *
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#qt">Queue Time</a>
     */
    public B addQueueTime() {
      return addQueueTime(System.currentTimeMillis());
    }

    /**
     * Adds the cache buster parameter. This can be used to prevent caching of URLs when using the
     * HTTP GET protocol.
     *
     * <p>It should be added as the last parameter in the URL.
     *
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#z">Cache Buster</a>
     */
    public B addCacheBuster() {
      return addParameter(CacheBusterParameter.getDefaultInstance());
    }

    ////////////////////////////////////////////////////////
    // User
    ////////////////////////////////////////////////////////

    /**
     * Adds the client id.
     *
     * <p>This field is required if User ID (uid) is not specified in the request.
     *
     * <p>The value of this field should be a random UUID (version 4) as described in <a
     * href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>.
     *
     * @param clientId the client id
     * @return the builder
     * @throws IllegalArgumentException if not a valid UUID
     * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
     */
    public B addClientId(String clientId) {
      return addParameter(new ClientIdParameter(clientId));
    }

    /**
     * Adds the client id.
     *
     * <p>This field is required if User ID (uid) is not specified in the request.
     *
     * @param clientId the client id
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
     */
    public B addClientId(UUID clientId) {
      return addParameter(new ClientIdParameter(clientId));
    }

    /**
     * Adds the user id.
     *
     * <p>This field is required if Client ID (cid) is not specified in the request.
     *
     * <p>It must not itself be PII (personally identifiable information).
     *
     * @param userId the user id
     * @return the builder
     * @throws IllegalArgumentException if the user id is empty
     * @see <a href="http://goo.gl/a8d4RP#uid">User Id</a>
     */
    public B addUserId(String userId) {
      ParameterUtils.requireNotEmpty(userId, "User Id is empty");
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.USER_ID, userId));
    }

    ////////////////////////////////////////////////////////
    // Session
    ////////////////////////////////////////////////////////

    /**
     * Adds the session control.
     *
     * <p>Used to control the session duration. A value of 'start' forces a new session to start
     * with this hit and 'end' forces the current session to end with this hit.
     *
     * @param sessionControl the session control
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#sc">Session Control</a>
     */
    public B addSessionControl(SessionControl sessionControl) {
      return addParameter(SessionControlParameter.create(sessionControl));
    }

    /**
     * Adds the IP override.
     *
     * <p>The IP address of the user. This should be a valid IP address in IPv4 or IPv6 format. It
     * will always be anonymized just as though aip (anonymize IP) had been used.
     *
     * @param ipAddress the IP address
     * @return the builder
     * @throws IllegalArgumentException If not a valid IP address
     * @see <a href="http://goo.gl/a8d4RP#uip">IP Override</a>
     */
    public B addIpOverride(String ipAddress) {
      ParameterUtils.validateIpAddress(ipAddress);
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.IP_OVERRIDE, ipAddress));
    }

    /**
     * Adds the user agent override.
     *
     * <p>The User Agent of the browser. Note that Google has libraries to identify real user
     * agents. Hand crafting your own agent could break at any time.
     *
     * @param userAgent the user agent
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#ua">User Agent Override</a>
     */
    public B addUserAgentOverride(String userAgent) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.USER_AGENT_OVERRIDE, userAgent));
    }

    /**
     * Adds the geographical override.
     *
     * <p>The geographical location of the user. The geographical ID should be a two letter country
     * code or a criteria ID representing a city or region (see
     * http://developers.google.com/analytics/devguides/collection/protocol/v1/geoid). This
     * parameter takes precedent over any location derived from IP address, including the IP
     * Override parameter. An invalid code will result in geographical dimensions to be set to '(not
     * set)'.
     *
     * @param geographicalLocation the geographical location
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#geoid">Geographical Override</a>
     */
    public B addGeographicalOverride(String geographicalLocation) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.GEOGRAPHICAL_OVERRIDE,
          geographicalLocation));
    }

    ////////////////////////////////////////////////////////
    // Traffic Sources
    ////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    // System Info
    ////////////////////////////////////////////////////////

    /**
     * Adds the screen resolution.
     *
     * @param width the width
     * @param height the height
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#sr">Screen Resolution</a>
     */
    public B addScreenResolution(int width, int height) {
      return addParameter(
          new ResolutionParameter(ProtocolSpecification.SCREEN_RESOLUTION, width, height));
    }

    /**
     * Adds the viewport size.
     *
     * @param width the width
     * @param height the height
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#vp">Viewport Size</a>
     */
    public B addViewportSize(int width, int height) {
      return addParameter(
          new ResolutionParameter(ProtocolSpecification.VIEWPORT_SIZE, width, height));
    }

    /**
     * Adds the document encoding.
     *
     * @param documentEncoding the document encoding
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#de">Document Encoding</a>
     */
    public B addDocumentEncoding(String documentEncoding) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.DOCUMENT_ENCODING, documentEncoding));
    }

    /**
     * Specifies the screen color depth.
     *
     * @param screenColorDepth the screen color depth
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#sd">Screen Colors</a>
     */
    public B addScreenColors(String screenColorDepth) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.SCREEN_COLORS, screenColorDepth));
    }

    /**
     * Adds the user language.
     *
     * @param locale the locale
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#ul">User Language</a>
     */
    public B addUserLanguage(Locale locale) {
      return addParameter(new UserLanguageParameter(locale));
    }

    /**
     * Adds the user language using the default locale.
     *
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#ul">User Language</a>
     */
    public B addUserLanguage() {
      return addParameter(new UserLanguageParameter());
    }

    /**
     * Specifies whether Java was enabled.
     *
     * @param javaEnabled the java enabled
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#je">Java Enabled</a>
     */
    public B addJavaEnabled(boolean javaEnabled) {
      return addParameter(
          new NoIndexBooleanParameter(ProtocolSpecification.JAVA_ENABLED, javaEnabled));
    }

    /**
     * Specifies the flash version.
     *
     * @param flashVersion the flash version
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#fl">Flash Version</a>
     */
    public B addFlashVersion(String flashVersion) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.FLASH_VERSION, flashVersion));
    }

    ////////////////////////////////////////////////////////
    // Hit
    ////////////////////////////////////////////////////////

    /**
     * Adds the hit type.
     *
     * @param hitType the hit type
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#t">Hit type</a>
     */
    public B addHitType(HitType hitType) {
      return addParameter(HitTypeParameter.create(hitType));
    }

    /**
     * Specifies that a hit be considered non-interactive.
     *
     * @param nonInteractive the non interactive
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#ni">Non-Interaction Hit</a>
     */
    public B addNonInteractionHit(boolean nonInteractive) {
      return addParameter(
          new NoIndexBooleanParameter(ProtocolSpecification.NON_INTERACTION_HIT, nonInteractive));
    }

    ////////////////////////////////////////////////////////
    // Content Information
    ////////////////////////////////////////////////////////

    /**
     * Use this parameter to send the full URL (document location) of the page on which content
     * resides.
     *
     * <p>For 'pageview' hits, either dl or both dh and dp have to be specified for the hit to be
     * valid.
     *
     * @param documentLocationUrl the document location url
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dl">Document Location Url</a>
     */
    public B addDocumentLocationUrl(String documentLocationUrl) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.DOCUMENT_LOCATION_URL,
          documentLocationUrl));
    }

    /**
     * Specifies the hostname from which content was hosted.
     *
     * <p>For 'pageview' hits, either dl or both dh and dp have to be specified for the hit to be
     * valid.
     *
     * @param documentHostName the document host name
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dh">Document Host Name</a>
     */
    public B addDocumentHostName(String documentHostName) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.DOCUMENT_HOST_NAME, documentHostName));
    }

    /**
     * The path portion of the page URL. Should begin with '/'.
     *
     * <p>For 'pageview' hits, either dl or both dh and dp have to be specified for the hit to be
     * valid.
     *
     * @param documentPath the document path
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dp">Document Path</a>
     */
    public B addDocumentPath(String documentPath) {
      ParameterUtils.validatePath(documentPath);
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.DOCUMENT_PATH, documentPath));
    }

    /**
     * The title of the page / document.
     *
     * @param documentTitle the document title
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dt">Document Title</a>
     */
    public B addDocumentTitle(String documentTitle) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.DOCUMENT_TITLE, documentTitle));
    }

    /**
     * This parameter is optional on web properties, and required on mobile properties for
     * screenview hits, where it is used for the 'Screen Name' of the screenview hit.
     *
     * <p>Required for screenview hit type.
     *
     * @param screenName the screen name
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#cd">Screen Name</a>
     */
    public B addScreenName(String screenName) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.SCREEN_NAME, screenName));
    }

    /**
     * Adds the content group.
     *
     * @param groupIndex the group index
     * @param contentGroup the content group
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#cg_">Content Group</a>
     */
    public B addContentGroup(int groupIndex, String contentGroup) {
      return addParameter(
          new OneIndexTextParameter(ProtocolSpecification.CONTENT_GROUP, groupIndex, contentGroup));
    }

    /**
     * The ID of a clicked DOM element.
     *
     * @param linkId the link id
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#linkid">Link ID</a>
     */
    public B addLinkeId(String linkId) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.LINK_ID, linkId));
    }

    ////////////////////////////////////////////////////////
    // App Tracking
    ////////////////////////////////////////////////////////

    /**
     * Adds the application name.
     *
     * <p>This field is required for any hit that has app related data (i.e., app version, app ID,
     * or app installer ID).
     *
     * @param applicationName the application name
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#an">Application Name</a>
     */
    public B addApplicationName(String applicationName) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.APPLICATION_NAME, applicationName));
    }

    /**
     * Adds the application identifier.
     *
     * @param applicationId the application id
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#aid">Application Id</a>
     */
    public B addApplicationId(String applicationId) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.APPLICATION_ID, applicationId));
    }

    /**
     * Adds the application version.
     *
     * @param applicationVersion the application version
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#av">Application Version</a>
     */
    public B addApplicationVersion(String applicationVersion) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.APPLICATION_VERSION, applicationVersion));
    }

    /**
     * Adds the application installer identifier.
     *
     * @param applicationInstallerId the application installer id
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#aiid">Application Installer ID</a>
     */
    public B addApplicationInstallerId(String applicationInstallerId) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.APPLICATION_INSTALLER_ID,
          applicationInstallerId));
    }

    ////////////////////////////////////////////////////////
    // Event Tracking
    ////////////////////////////////////////////////////////

    /**
     * Adds the event category.
     *
     * <p>Required for event hit type. Specifies the event category. Must not be empty.
     *
     * @param eventCategory the event category
     * @return the builder
     * @throws IllegalArgumentException If the event category is empty
     * @see <a href="http://goo.gl/a8d4RP#ec">Event Category</a>
     */
    public B addEventCategory(String eventCategory) {
      ParameterUtils.requireNotEmpty(eventCategory, "Event category is empty");
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.EVENT_CATEGORY, eventCategory));
    }

    /**
     * Adds the event action.
     *
     * <p>Required for event hit type. Specifies the event category. Must not be empty.
     *
     * @param eventAction the event action
     * @return the builder
     * @throws IllegalArgumentException If the event action is empty
     * @see <a href="http://goo.gl/a8d4RP#ea">Event Action</a>
     */
    public B addEventAction(String eventAction) {
      ParameterUtils.requireNotEmpty(eventAction, "Event action is empty");
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.EVENT_ACTION, eventAction));
    }

    /**
     * Adds the event label.
     *
     * @param eventLabel the event label
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#el">Event Label</a>
     */
    public B addEventLabel(String eventLabel) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.EVENT_LABEL, eventLabel));
    }

    /**
     * Adds the event value.
     *
     * <p>Values must be non-negative.
     *
     * @param eventValue the event value
     * @return the builder
     * @throws IllegalArgumentException If the event value is negative
     * @see <a href="http://goo.gl/a8d4RP#ev">Event Value</a>
     */
    public B addEventValue(int eventValue) {
      ParameterUtils.requirePositive(eventValue, "Event value");
      return addParameter(new NoIndexIntParameter(ProtocolSpecification.EVENT_VALUE, eventValue));
    }

    ////////////////////////////////////////////////////////
    // E-Commerce
    ////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    // Enhanced E-Commerce
    ////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////
    // Social Interactions
    ////////////////////////////////////////////////////////

    /**
     * Adds the social network.
     *
     * @param socialNetwork the social network
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#sn">Social Network</a>
     */
    public B addSocialNetwork(String socialNetwork) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.SOCIAL_NETWORK, socialNetwork));
    }

    /**
     * Adds the social action.
     *
     * @param socialAction the social action
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#sa">Social Action</a>
     */
    public B addSocialAction(String socialAction) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.SOCIAL_ACTION, socialAction));
    }

    /**
     * Adds the social action target.
     *
     * @param socialActionTarget the social action target
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#st">Social Action Target</a>
     */
    public B addSocialActionTarget(String socialActionTarget) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.SOCIAL_ACTION_TARGET, socialActionTarget));
    }

    ////////////////////////////////////////////////////////
    // Timing
    ////////////////////////////////////////////////////////

    /**
     * Adds the user timing category.
     *
     * <p>Required for a timing hit.
     *
     * @param userTimingCategory the user timing category
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#utc">User timing category</a>
     */
    public B addUserTimingCategory(String userTimingCategory) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.USER_TIMING_CATEGORY, userTimingCategory));
    }

    /**
     * Adds the user timing variable name.
     *
     * <p>Required for a timing hit.
     *
     * @param userTimingVariableName the user timing variable name
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#utv">User timing variable name</a>
     */
    public B addUserTimingVariableName(String userTimingVariableName) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.USER_TIMING_VARIABLE_NAME,
          userTimingVariableName));
    }

    /**
     * Adds the user timing time.
     *
     * <p>Required for a timing hit.
     *
     * @param userTimingTime the user timing time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#utt">User timing time</a>
     */
    public B addUserTimingTime(int userTimingTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.USER_TIMING_TIME, userTimingTime));
    }

    /**
     * Adds the user timing label.
     *
     * @param userTimingLabel the user timing label
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#utl">User timing label</a>
     */
    public B addUserTimingLabel(String userTimingLabel) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.USER_TIMING_LABEL, userTimingLabel));
    }

    /**
     * Adds the page load time.
     *
     * @param pageLoadTime the page load time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#plt">Page load time</a>
     */
    public B addPageLoadTime(int pageLoadTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.PAGE_LOAD_TIME, pageLoadTime));
    }

    /**
     * Adds the DNS time.
     *
     * @param dnsTime the DNS time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dns">DNS time</a>
     */
    public B addDnsTime(int dnsTime) {
      return addParameter(new NoIndexIntParameter(ProtocolSpecification.DNS_TIME, dnsTime));
    }

    /**
     * Adds the page download time.
     *
     * @param pageDownloadTime the page download time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#pdt">Page download time</a>
     */
    public B addPageDownloadTime(int pageDownloadTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.PAGE_DOWNLOAD_TIME, pageDownloadTime));
    }

    /**
     * Adds the redirect response time.
     *
     * @param redirectResponseTime the redirect response time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#rrt">Redirect response time</a>
     */
    public B addRedirectResponseTime(int redirectResponseTime) {
      return addParameter(new NoIndexIntParameter(ProtocolSpecification.REDIRECT_RESPONSE_TIME,
          redirectResponseTime));
    }

    /**
     * Adds the TCP connect time.
     *
     * @param tcpConnectTime the TCP connect time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#tcp">TCP connect time</a>
     */
    public B addTcpConnectTime(int tcpConnectTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.TCP_CONNECT_TIME, tcpConnectTime));
    }

    /**
     * Adds the server response time.
     *
     * @param serverResponseTime the server response time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#srt">Server response time</a>
     */
    public B addServerResponseTime(int serverResponseTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.SERVER_RESPONSE_TIME, serverResponseTime));
    }

    /**
     * Adds the DOM interactive time.
     *
     * @param domInteractiveTime the DOM interactive time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#dit">DOM interactive time</a>
     */
    public B addDomInteractiveTime(int domInteractiveTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.DOM_INTERACTIVE_TIME, domInteractiveTime));
    }

    /**
     * Adds the content load time.
     *
     * @param contentLoadTime the content load time
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#clt">Content load time</a>
     */
    public B addContentLoadTime(int contentLoadTime) {
      return addParameter(
          new NoIndexIntParameter(ProtocolSpecification.CONTENT_LOAD_TIME, contentLoadTime));
    }

    ////////////////////////////////////////////////////////
    // Exceptions
    ////////////////////////////////////////////////////////

    /**
     * Specifies the description of an exception.
     *
     * @param exceptionDescription the exception description
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#exd">Exception Description</a>
     */
    public B addExceptionDescription(String exceptionDescription) {
      return addParameter(new NoIndexTextParameter(ProtocolSpecification.EXCEPTION_DESCRIPTION,
          exceptionDescription));
    }

    /**
     * Specifies whether the exception was fatal.
     *
     * @param exceptionFatal the exception fatal
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#exf">Is Exception Fatal</a>
     */
    public B addIsExceptionFatal(boolean exceptionFatal) {
      return addParameter(
          new NoIndexBooleanParameter(ProtocolSpecification.IS_EXCEPTION_FATAL, exceptionFatal));
    }

    ////////////////////////////////////////////////////////
    // Custom Dimensions/Metrics
    ////////////////////////////////////////////////////////

    /**
     * Adds the custom dimension.
     *
     * @param index the index
     * @param value the value
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#cd_">Custom Dimension</a>
     */
    public B addCustomDimension(int index, String value) {
      return addParameter(
          new OneIndexTextParameter(ProtocolSpecification.CUSTOM_DIMENSION, index, value));
    }

    /**
     * Adds the custom metric.
     *
     * @param index the index
     * @param value the value
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#cm_">Custom Metric</a>
     */
    public B addCustomMetric(int index, int value) {
      return addParameter(
          new OneIndexNumberParameter(ProtocolSpecification.CUSTOM_METRIC, index, value));
    }

    ////////////////////////////////////////////////////////
    // Content Experiments
    ////////////////////////////////////////////////////////

    /**
     * Adds the experiment id.
     *
     * @param experimentId the experiment id
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#xid">Experiment Id</a>
     */
    public B addExperimentId(String experimentId) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.EXPERIMENT_ID, experimentId));
    }

    /**
     * Adds the experiment variant.
     *
     * @param experimentVariant the experiment variant
     * @return the builder
     * @see <a href="http://goo.gl/a8d4RP#xvar">Experiment Variant</a>
     */
    public B addExperimentVariant(String experimentVariant) {
      return addParameter(
          new NoIndexTextParameter(ProtocolSpecification.EXPERIMENT_VARIANT, experimentVariant));
    }
  }

  ////////////////////////////////////////////////////////
  // Public builders.
  ////////////////////////////////////////////////////////
  // These set the type for the ParametersBuilder<B> so
  // the object method API drops the generic recursion
  // and is cleaner.
  ////////////////////////////////////////////////////////

  // @formatter:off
  /**
   * A builder for {@link Parameters} required for every hit.
   *
   * <p>This is a special builder that ensures that:
   *
   * <ul>
   * <li>The protocol version is set (required for all hits).
   * <li>The tracking Id is set (required for all hits).
   * <li>The client Id can be added only once.
   * <li>The user Id can be added only once.
   * <li>The client Id is a random UUID if both the client Id and user Id are unset.
   * </ul>
   *
   * <p>Note: Either the client Id or user Id are required for all hits.
   *
   * <p>This builder should be used to construct parameters that are sent with every hit.
   */
  // @formatter:on
  public static class RequiredBuilder extends ParametersBuilder<RequiredBuilder> {

    /** Flag to indicate the client Id has been set. */
    private boolean hasClientId;
    /** Flag to indicate the user Id has been set. */
    private boolean hasUserId;

    /**
     * Create a new builder.
     *
     * @param trackingId the tracking id
     */
    private RequiredBuilder(String trackingId) {
      super(RequiredBuilder.class);
      addVersion();
      // This is true since this is Java
      addJavaEnabled(true);
      addTrackingId(trackingId);
    }

    @Override
    public RequiredBuilder addClientId(String clientId) {
      if (hasClientId) {
        throw new IllegalArgumentException("Duplicate client Id");
      }
      hasClientId = true;
      return super.addClientId(clientId);
    }

    @Override
    public RequiredBuilder addClientId(UUID clientId) {
      if (hasClientId) {
        throw new IllegalArgumentException("Duplicate client Id");
      }
      hasClientId = true;
      return super.addClientId(clientId);
    }

    @Override
    public RequiredBuilder addUserId(String userId) {
      if (hasUserId) {
        throw new IllegalArgumentException("Duplicate user Id");
      }
      hasUserId = true;
      return super.addUserId(userId);
    }

    @Override
    public Parameters build() {
      // Create a random client Id if required
      if (!(hasClientId || hasUserId)) {
        addClientId(UUID.randomUUID());
      }
      return super.build();
    }
  }

  // @formatter:off
  /**
   * A builder for {@link Parameters} that are in addition to the parameters required for every hit.
   *
   * <p>This is a special builder that ensures that the following methods do nothing:
   *
   * <ul>
   * <li>{@link #addVersion()}
   * <li>{@link #addTrackingId(String)}
   * <li>{@link #addClientId(String)}
   * <li>{@link #addClientId(UUID)}
   * <li>{@link #addUserId(String)}
   * </ul>
   *
   * <p>This builder should be used to construct a partial subset of parameters that are sent in
   * addition to the required protocol parameters version, tracking Id, client Id and/or user Id
   * parameters.
   *
   * <p>Provides a utility method that allows the builder to return it's parent.
   *
   * @param <P> the type of the parent
   * @see RequiredBuilder
   */
  // @formatter:on
  public static class PartialBuilder<P> extends ParametersBuilder<PartialBuilder<P>> {

    /** The parent. */
    private final P parent;

    /**
     * Create a new builder.
     *
     * @param parent the parent
     */
    private PartialBuilder(P parent) {
      super(PartialBuilder.class);
      this.parent = parent;
    }

    @Override
    public PartialBuilder<P> addVersion() {
      return self;
    }

    @Override
    public PartialBuilder<P> addTrackingId(String trackingId) {
      return self;
    }

    @Override
    public PartialBuilder<P> addClientId(String clientId) {
      return self;
    }

    @Override
    public PartialBuilder<P> addClientId(UUID clientId) {
      return self;
    }

    @Override
    public PartialBuilder<P> addUserId(String userId) {
      return self;
    }

    /**
     * Gets the parent.
     *
     * <p>The parent is set during construction allowing this builder to be chained.
     *
     * @return the parent
     */
    public P getParent() {
      return parent;
    }
  }

  /**
   * A builder for {@link Parameters}.
   *
   * <p>This is a generic builder with no defaults that exposes the parameter API. It can be used to
   * construct partial or complete hit parameters.
   *
   * <p>Note that other specialised builders are provided that add restrictions to prevent common
   * misuse cases.
   *
   * @see RequiredBuilder
   * @see PartialBuilder
   */
  public static class Builder extends ParametersBuilder<Builder> {

    /**
     * Create a new builder.
     */
    private Builder() {
      super(Builder.class);
    }
  }

  /**
   * A builder for {@link Parameters} for a <strong>single</strong> Google Analytics hit.
   *
   * <p>This is a special builder that ensures that the hit type is set and a timestamp of the hit
   * is recorded.
   *
   * <p>No restrictions are made on the parameter API so the builder can be used to create a
   * complete hit or partial hit to be combined with the required parameters: version; tracking Id;
   * client/user Id.
   *
   * <p>The abstract class provides a method to send the hit so should be extended with an
   * implementation that handles the send request.
   *
   * @param <T> the result type returned by the send method
   */
  public abstract static class HitBuilder<T> extends ParametersBuilder<HitBuilder<T>> {

    /** The timestamp when the hit was created. */
    private final long timestamp;

    /**
     * Creates a new hit builder.
     *
     * <p>The provided formatted parameter is added as the first parameter if it is not null. It can
     * be used to specify parameters that are required for each hit.
     *
     * @param formattedParameter the formatted parameter
     * @param hitType the hit type
     * @param timestamp the timestamp
     */
    protected HitBuilder(FormattedParameter formattedParameter, HitTypeParameter hitType,
        long timestamp) {
      super(HitBuilder.class);
      this.timestamp = timestamp;
      if (formattedParameter != null) {
        add(formattedParameter);
      }
      add(hitType);
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
      return timestamp;
    }

    /**
     * Send the hit.
     *
     * <p>Note: The implementation of the send method is dependent on the sub-class.
     *
     * @return the result
     */
    public abstract T send();
  }
}
