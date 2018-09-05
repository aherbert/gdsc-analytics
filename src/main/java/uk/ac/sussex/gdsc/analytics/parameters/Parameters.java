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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Contains Google Analytics Measurement Protocol parameters.
 * 
 * <p>Note that although not enforced the set of {@code name=value} URL parameters should have
 * unique {@code name} tags. If not the hit may not be correctly processed by Google Analytics.
 *
 * @see <a href=
 *      "https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters">Measurement
 *      Protocol Parameter Reference</a>
 */
public class Parameters implements FormattedParameter {

  /** The formatted parameters. */
  private final FormattedParameter[] formattedParameters;

  /**
   * Create a new instance.
   *
   * @param formattedParameters the formatted parameters
   */
  Parameters(FormattedParameter[] formattedParameters) {
    this.formattedParameters = formattedParameters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * uk.ac.sussex.gdsc.analytics.parameters.FormattedParameter#formatTo(java.lang.StringBuilder)
   */
  @Override
  public void appendTo(StringBuilder sb) {
    for (final FormattedParameter fp : formattedParameters) {
      fp.appendTo(sb);
    }
  }

  /**
   * Create a new {@link ClientParametersBuilder} with the given Google Analytics tracking id.
   * 
   * <p>The format is UA-XXXX-Y.
   * 
   * <p>This is a specialist builder to ensure the the tracking Id and client/user Id required for
   * all hits are included.
   *
   * @param trackingId the tracking id
   * @return the builder
   * @throws IllegalArgumentException if tracking ID is invalid
   * @see <a
   *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#tid">Tracking
   *      Id</a>
   */
  public static ClientParametersBuilder newClientParametersBuilder(String trackingId)
      throws IllegalArgumentException {
    return new ClientParametersBuilder(trackingId);
  }

  /**
   * Create a new {@link NonClientParametersBuilder}.
   * 
   * <p>This is a specialist builder to ensure the the tracking Id and client/user Id are not
   * duplicated when adding additional parameters.
   * 
   * @return the builder
   */
  public static NonClientParametersBuilder newNonClientParametersBuilder() {
    return new NonClientParametersBuilder();
  }

  /**
   * Create a new {@link GenericParametersBuilder}.
   * 
   * <p>This is a generic builder with no defaults that exposes the parameter API. It can be used to
   * construct partial or complete hit parameters.
   *
   * @return the builder
   */
  public static GenericParametersBuilder newGenericParametersBuilder() {
    return new GenericParametersBuilder();
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
  static class ParametersBuilder<B extends ParametersBuilder<B>> {

    /** The forward slash character '/'. */
    private static final char FORWARDSLASH = '/';

    /**
     * A reference to '{@code this}' cast to the appropriate {@code [this]} type.
     * 
     * <p>This can be returned from any builder method, i.e. {@code return self; }
     */
    protected final B self;

    /** The list. */
    private final List<FormattedParameter> list = new ArrayList<>();

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
     * Builds the {@link Parameters}.
     *
     * @return the parameters
     */
    public Parameters build() {
      return new Parameters(list.toArray(new FormattedParameter[list.size()]));
    }

    /**
     * Adds the formatted parameter.
     *
     * @param parameter the parameter
     * @return the builder
     */
    public B add(FormattedParameter parameter) {
      list.add(Objects.requireNonNull(parameter, "Parameter is null"));
      return self;
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
    public B add(String name, String value) throws IllegalArgumentException {
      ParameterUtils.requireNotEmpty(name, "name is empty");
      list.add(new StringParameter(name, value));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#v">Protocol
     *      Version</a>
     */
    protected B addVersion() {
      list.add(Version1Parameter.getDefaultInstance());
      return self;
    }

    /**
     * Adds the tracking id.
     * 
     * <p>The format is UA-XXXX-Y.
     *
     * @param trackingId the tracking id
     * @return the builder
     * @throws IllegalArgumentException if tracking ID is invalid
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#tid">Tracking
     *      Id</a>
     */
    protected B addTrackingId(String trackingId) throws IllegalArgumentException {
      list.add(new TrackingIdParameter(trackingId));
      return self;
    }

    /**
     * Adds the anonymize IP.
     * 
     * <p>When present, the IP address of the sender will be anonymized.
     *
     * @param anonymizeIp the anonymize ip
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#aip">Anonymize
     *      IP</a>
     */
    public B addAnonymizeIp(boolean anonymizeIp) {
      list.add(new BooleanParameter("&aip", anonymizeIp));
      return self;
    }

    /**
     * Adds the data source.
     *
     * @param dataSource the data source
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ds">Data
     *      Source</a>
     */
    public B addDataSource(String dataSource) {
      list.add(new StringParameter("&ds", dataSource));
      return self;
    }

    /**
     * Adds the queue time.
     * 
     * <p>The value represents the time delta (in milliseconds) between when the hit being reported
     * occurred and the time the hit was sent.
     *
     * @param queueTime the queue time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#qt">Queue
     *      Time</a>
     */
    protected B addQueueTime(int queueTime) {
      list.add(new QueueTimeParameter(queueTime));
      return self;
    }

    /**
     * Adds the cache buster parameter. This can be used to prevent caching of URLs when using the
     * HTTP GET protocol.
     * 
     * <p>It should be added as the last parameter in the URL.
     *
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#z">Cache
     *      Buster</a>
     */
    protected B addCacheBuster() {
      list.add(new CacheBusterParameter());
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cid">Client
     *      Id</a>
     */
    protected B addClientId(String clientId) throws IllegalArgumentException {
      list.add(new ClientIdParameter(clientId));
      return self;
    }

    /**
     * Adds the client id.
     * 
     * <p>This field is required if User ID (uid) is not specified in the request.
     *
     * @param clientId the client id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cid">Client
     *      Id</a>
     */
    protected B addClientId(UUID clientId) {
      list.add(new ClientIdParameter(clientId));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#uid">User
     *      Id</a>
     */
    protected B addUserId(String userId) throws IllegalArgumentException {
      ParameterUtils.requireNotEmpty(userId, "User Id is empty");
      list.add(new StringParameter("&uid", userId));
      return self;
    }

    ////////////////////////////////////////////////////////
    // Session
    ////////////////////////////////////////////////////////

    /**
     * Adds the session control.
     *
     * @param sessionControl the session control
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#sc">Session
     *      Control</a>
     */
    public B addSessionControl(SessionControlParameter sessionControl) {
      list.add(Objects.requireNonNull(sessionControl, "Session control is null"));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#sr">Screen
     *      Resolution</a>
     */
    public B addScreenResolution(int width, int height) {
      list.add(new ResolutionParameter("&sr", width, height));
      return self;
    }

    /**
     * Adds the screen resolution using system information.
     *
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#sr">Screen
     *      Resolution</a>
     * @see SystemUtils#getScreenSize()
     */
    public B addScreenResolution() {
      final Dimension d = SystemUtils.getScreenSize();
      if (d != null) {
        list.add(new ResolutionParameter("&sr", d.width, d.height));
      }
      return self;
    }

    /**
     * Adds the viewport size.
     *
     * @param width the width
     * @param height the height
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#vp">Viewport
     *      Size</a>
     */
    public B addViewportSize(int width, int height) {
      list.add(new ResolutionParameter("&vp", width, height));
      return self;
    }

    /**
     * Adds the document encoding.
     *
     * @param documentEncoding the document encoding
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#de">Document
     *      Encoding</a>
     */
    public B addDocumentEncoding(String documentEncoding) {
      list.add(new StringParameter("&de", documentEncoding));
      return self;
    }

    /**
     * Specifies the screen color depth.
     *
     * @param screenColorDepth the screen color depth
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#sd">Screen
     *      Colors</a>
     */
    public B addScreenColors(String screenColorDepth) {
      list.add(new StringParameter("&sd", screenColorDepth));
      return self;
    }

    /**
     * Adds the user language.
     *
     * @param userLanguage the user language
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ul">User
     *      Language</a>
     */
    public B addUserLanguage(String userLanguage) {
      list.add(new UserLanguageParameter(userLanguage));
      return self;
    }

    /**
     * Adds the user language using the default locale.
     *
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ul">User
     *      Language</a>
     */
    public B addUserLanguage() {
      list.add(new UserLanguageParameter());
      return self;
    }

    /**
     * Specifies whether Java was enabled.
     *
     * @param javaEnabled the java enabled
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#je">Java
     *      Enabled</a>
     */
    public B addJavaEnabled(boolean javaEnabled) {
      list.add(new BooleanParameter("&je", javaEnabled));
      return self;
    }

    /**
     * Specifies the flash version.
     *
     * @param flashVersion the flash version
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#fl">Flash
     *      Version</a>
     */
    public B addFlashVersion(String flashVersion) {
      list.add(new StringParameter("&fl", flashVersion));
      return self;
    }

    ////////////////////////////////////////////////////////
    // Hit
    ////////////////////////////////////////////////////////

    /**
     * Adds the hit type.
     *
     * @param hitType the hit type
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#t">Hit
     *      type</a>
     */
    public B addHitType(HitTypeParameter hitType) {
      list.add(Objects.requireNonNull(hitType, "Hit type is null"));
      return self;
    }

    /**
     * Specifies that a hit be considered non-interactive.
     *
     * @param nonInteractive the non interactive
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ni">Non-Interaction
     *      Hit</a>
     */
    public B addNonInteractionHit(boolean nonInteractive) {
      list.add(new BooleanParameter("&ni", nonInteractive));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dl">Document
     *      Location Url</a>
     */
    public B addDocumentLocationUrl(String documentLocationUrl) {
      list.add(new StringParameter("&dl", documentLocationUrl));
      return self;
    }

    /**
     * Specifies the hostname from which content was hosted.
     * 
     * <p>For 'pageview' hits, either dl or both dh and dp have to be specified for the hit to be
     * valid.
     *
     * @param documentHostName the document host name
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dl">Document
     *      Host Name</a>
     */
    public B addDocumentHostName(String documentHostName) {
      list.add(new StringParameter("&dh", documentHostName));
      return self;
    }

    /**
     * The path portion of the page URL. Should begin with '/'.
     * 
     * <p>For 'pageview' hits, either dl or both dh and dp have to be specified for the hit to be
     * valid.
     *
     * @param documentPath the document path
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dp">Document
     *      Path</a>
     */
    public B addDocumentPath(String documentPath) {
      ParameterUtils.requireNotEmpty(documentPath, "Document path is empty");
      if (documentPath.charAt(0) != FORWARDSLASH) {
        throw new IllegalArgumentException("Document path should begin with '/'");
      }
      list.add(new StringParameter("&dp", documentPath));
      return self;
    }

    /**
     * The title of the page / document.
     *
     * @param documentTitle the document title
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dt">Document
     *      Title</a>
     */
    public B addDocumentTitle(String documentTitle) {
      list.add(new StringParameter("&dt", documentTitle));
      return self;
    }

    /**
     * This parameter is optional on web properties, and required on mobile properties for
     * screenview hits, where it is used for the 'Screen Name' of the screenview hit.
     * 
     * <p>Required for screenview hit type.
     *
     * @param screenName the screen name
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cd">Screen
     *      Name</a>
     */
    public B addScreenName(String screenName) {
      list.add(new StringParameter("&cd", screenName));
      return self;
    }

    /**
     * Adds the content group.
     *
     * @param groupIndex the group index
     * @param contentGroup the content group
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cg_">Content
     *      Group</a>
     */
    public B addContentGroup(int groupIndex, String contentGroup) {
      list.add(new IndexedStringParameter("&cg", groupIndex, contentGroup));
      return self;
    }

    /**
     * The ID of a clicked DOM element.
     *
     * @param linkId the link id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#linkid">Link
     *      ID</a>
     */
    public B addLinkeId(String linkId) {
      list.add(new StringParameter("&linkid", linkId));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#an">Application
     *      Name</a>
     */
    public B addApplicationName(String applicationName) {
      list.add(new StringParameter("&an", applicationName));
      return self;
    }

    /**
     * Adds the application identifier.
     *
     * @param applicationId the application id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#aid">Application
     *      Id</a>
     */
    public B addApplicationId(String applicationId) {
      list.add(new StringParameter("&aid", applicationId));
      return self;
    }

    /**
     * Adds the application version.
     *
     * @param applicationVersion the application version
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#av">Application
     *      Version</a>
     */
    public B addApplicationVersion(String applicationVersion) {
      list.add(new StringParameter("&av", applicationVersion));
      return self;
    }

    /**
     * Adds the application installer identifier.
     *
     * @param applicationInstallerId the application installer id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#aiid">Application
     *      Installer ID</a>
     */
    public B addApplicationInstallerId(String applicationInstallerId) {
      list.add(new StringParameter("&aiid", applicationInstallerId));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ec">Event
     *      Category</a>
     */
    public B addEventCategory(String eventCategory) throws IllegalArgumentException {
      ParameterUtils.requireNotEmpty(eventCategory, "Event category is empty");
      list.add(new StringParameter("&ec", eventCategory));
      return self;
    }

    /**
     * Adds the event action.
     * 
     * <p>Required for event hit type. Specifies the event category. Must not be empty.
     *
     * @param eventAction the event action
     * @return the builder
     * @throws IllegalArgumentException If the event action is empty
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ea">Event
     *      Action</a>
     */
    public B addEventAction(String eventAction) throws IllegalArgumentException {
      ParameterUtils.requireNotEmpty(eventAction, "Event action is empty");
      list.add(new StringParameter("&ea", eventAction));
      return self;
    }

    /**
     * Adds the event label.
     *
     * @param eventLabel the event label
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#el">Event
     *      Label</a>
     */
    public B addEventLabel(String eventLabel) {
      list.add(new StringParameter("&el", eventLabel));
      return self;
    }

    /**
     * Adds the event value.
     * 
     * <p>Values must be non-negative.
     *
     * @param eventValue the event value
     * @return the builder
     * @throws IllegalArgumentException If the event value is negative
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#ev">Event
     *      Value</a>
     */
    public B addEventValue(int eventValue) throws IllegalArgumentException {
      ParameterUtils.requirePositive(eventValue, "Event value");
      list.add(new IntParameter("&ev", eventValue));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#utc">User
     *      timing category</a>
     */
    public B addUserTimingCategory(String userTimingCategory) {
      list.add(new StringParameter("&utc", userTimingCategory));
      return self;
    }

    /**
     * Adds the user timing variable name.
     * 
     * <p>Required for a timing hit.
     *
     * @param userTimingVariableName the user timing variable name
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#utv">User
     *      timing variable name</a>
     */
    public B addUserTimingVariableName(String userTimingVariableName) {
      list.add(new StringParameter("&utv", userTimingVariableName));
      return self;
    }

    /**
     * Adds the user timing time.
     *
     * <p>Required for a timing hit.
     * 
     * @param userTimingTime the user timing time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#utt">User
     *      timing time</a>
     */
    public B addUserTimingTime(int userTimingTime) {
      list.add(new IntParameter("&utt", userTimingTime));
      return self;
    }

    /**
     * Adds the user timing label.
     *
     * @param userTimingLabel the user timing label
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#utl">User
     *      timing label</a>
     */
    public B addUserTimingLabel(String userTimingLabel) {
      list.add(new StringParameter("&utl", userTimingLabel));
      return self;
    }

    /**
     * Adds the page load time.
     *
     * @param pageLoadTime the page load time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#plt">Page
     *      load time</a>
     */
    public B addPageLoadTime(int pageLoadTime) {
      list.add(new IntParameter("&plt", pageLoadTime));
      return self;
    }

    /**
     * Adds the DNS time.
     *
     * @param dnsTime the DNS time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dns">DNS
     *      time</a>
     */
    public B addDnsTime(int dnsTime) {
      list.add(new IntParameter("&dns", dnsTime));
      return self;
    }

    /**
     * Adds the page download time.
     *
     * @param pageDownloadTime the page download time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#pdt">Page
     *      download time</a>
     */
    public B addPageDownloadTime(int pageDownloadTime) {
      list.add(new IntParameter("&pdt", pageDownloadTime));
      return self;
    }

    /**
     * Adds the redirect response time.
     *
     * @param redirectResponseTime the redirect response time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#rrt">Redirect
     *      response time</a>
     */
    public B addRedirectResponseTime(int redirectResponseTime) {
      list.add(new IntParameter("&rrt", redirectResponseTime));
      return self;
    }

    /**
     * Adds the TCP connect time.
     *
     * @param tcpConnectTime the TCP connect time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#tcp">TCP
     *      connect time</a>
     */
    public B addTcpConnectTime(int tcpConnectTime) {
      list.add(new IntParameter("&tcp", tcpConnectTime));
      return self;
    }

    /**
     * Adds the server response time.
     *
     * @param serverResponseTime the server response time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#srt">Server
     *      response time</a>
     */
    public B addServerResponseTime(int serverResponseTime) {
      list.add(new IntParameter("&srt", serverResponseTime));
      return self;
    }

    /**
     * Adds the DOM interactive time.
     *
     * @param domInteractiveTime the DOM interactive time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#dit">DOM
     *      interactive time</a>
     */
    public B addDomInteractiveTime(int domInteractiveTime) {
      list.add(new IntParameter("&dit", domInteractiveTime));
      return self;
    }

    /**
     * Adds the content load time.
     *
     * @param contentLoadTime the content load time
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#clt">Content
     *      load time</a>
     */
    public B addContentLoadTime(int contentLoadTime) {
      list.add(new IntParameter("&clt", contentLoadTime));
      return self;
    }

    ////////////////////////////////////////////////////////
    // Exceptions
    ////////////////////////////////////////////////////////

    /**
     * Specifies the description of an exception.
     *
     * @param exceptionDescription the exception description
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#exd">Exception
     *      Description</a>
     */
    public B addExceptionDescription(String exceptionDescription) {
      list.add(new StringParameter("&exd", exceptionDescription));
      return self;
    }

    /**
     * Specifies whether the exception was fatal.
     *
     * @param exceptionFatal the exception fatal
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#exd">Is
     *      Exception Fatal</a>
     */
    public B addIsExceptionFatal(boolean exceptionFatal) {
      list.add(new BooleanParameter("&exf", exceptionFatal));
      return self;
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
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cd_">Custom
     *      Dimension</a>
     */
    public B addCustomDimension(int index, String value) {
      list.add(new CustomDimensionParameter(index, value));
      return self;
    }

    /**
     * Adds the custom metric.
     *
     * @param index the index
     * @param value the value
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cm_">Custom
     *      Metric</a>
     */
    public B addCustomMetric(int index, int value) {
      list.add(new CustomMetricParameter(index, value));
      return self;
    }

    ////////////////////////////////////////////////////////
    // Content Experiments
    ////////////////////////////////////////////////////////
  }

  /**
   * A builder for {@link Parameters}.
   * 
   * <p>This is a special builder that ensures that the tracking Id, client Id and user Id cannot be
   * set.
   * 
   * <p>This builder should be used to construct a subset of parameters that are sent in addition to
   * the tracking Id, client Id and/or user Id parameters.
   * 
   * @see ClientParametersBuilder
   */
  public static class NonClientParametersBuilder
      extends ParametersBuilder<NonClientParametersBuilder> {
    /**
     * Create a new builder.
     */
    NonClientParametersBuilder() {
      super(NonClientParametersBuilder.class);
    }
  }

  // @formatter:off
  /**
   * A builder for {@link Parameters}.
   * 
   * <p>This is a special builder that ensures that:
   * 
   * <ul> 
   * <li>The protocol version is set (required for all hits)
   * <li>The tracking Id is set (required for all hits)
   * <li>The client Id can be added only once
   * <li>The user Id can be added only once
   * <li>The client Id is a random UUID if both the client Id and user Id are unset
   * </ul>
   *
   * <p>Note: Either the client Id or user Id are required for all hits.
   * 
   * <p>This builder should be used to construct parameters that are sent with every hit.
   */
  // @formatter:on
  public static class ClientParametersBuilder extends ParametersBuilder<ClientParametersBuilder> {

    /** Flag to indicate the client Id has been set. */
    private boolean hasClientId;
    /** Flag to indicate the user Id has been set. */
    private boolean hasUserId;

    /**
     * Create a new builder.
     *
     * @param trackingId the tracking id
     */
    ClientParametersBuilder(String trackingId) {
      super(ClientParametersBuilder.class);
      addVersion();
      // This is true since this is Java
      addJavaEnabled(true);
      addTrackingId(trackingId);
    }

    @Override
    public ClientParametersBuilder addClientId(String clientId) throws IllegalArgumentException {
      if (hasClientId) {
        throw new IllegalArgumentException("Duplicate client Id");
      }
      hasClientId = true;
      return super.addClientId(clientId);
    }

    @Override
    public ClientParametersBuilder addClientId(UUID clientId) {
      if (hasClientId) {
        throw new IllegalArgumentException("Duplicate client Id");
      }
      hasClientId = true;
      return super.addClientId(clientId);
    }

    @Override
    public ClientParametersBuilder addUserId(String userId) throws IllegalArgumentException {
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

  /**
   * A builder for {@link Parameters} for a <strong>single</strong> Google Analytics hit.
   * 
   * <p>This is a special builder that ensures that the hit type is set and a timestamp of the hit
   * is recorded.
   * 
   * <p>The builder will create partial parameters for a hit and should be combined with the
   * required parameters: version; tracking Id; client/user Id.
   * 
   * <p>The abstract class provides a method to send the hit so should be extended with an
   * implementation that handles the send request.
   *
   * @param <T> the result type returned by the send method
   */
  public abstract static class HitBuilder<T> extends Parameters.ParametersBuilder<HitBuilder<T>> {

    /** The timestamp when the hit was created. */
    private final long timestamp;

    /**
     * Creates a new hit builder.
     *
     * @param hitType the hit type
     * @param timestamp the timestamp
     */
    protected HitBuilder(HitTypeParameter hitType, long timestamp) {
      super(HitBuilder.class);
      this.timestamp = timestamp;
      addHitType(hitType);
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

  /**
   * A builder for {@link Parameters}.
   * 
   * <p>This is a generic builder with no defaults that exposes the parameter API. It can be used to
   * construct partial or complete hit parameters.
   * 
   * <p>Note that other specialised builders are provided that add restrictions to prevent common
   * misuse cases.
   * 
   * @see ClientParametersBuilder
   * @see NonClientParametersBuilder
   */
  public static class GenericParametersBuilder extends ParametersBuilder<GenericParametersBuilder> {

    /**
     * Create a new builder.
     */
    GenericParametersBuilder() {
      super(GenericParametersBuilder.class);
    }

    ////////////////////////////////////////////////////////
    // Expose protected methods
    ////////////////////////////////////////////////////////

    @Override
    public GenericParametersBuilder addCacheBuster() {
      return super.addCacheBuster();
    }

    @Override
    public GenericParametersBuilder addClientId(String clientId) throws IllegalArgumentException {
      return super.addClientId(clientId);
    }

    @Override
    public GenericParametersBuilder addClientId(UUID clientId) {
      return super.addClientId(clientId);
    }

    @Override
    public GenericParametersBuilder addQueueTime(int queueTime) {
      return super.addQueueTime(queueTime);
    }

    @Override
    public GenericParametersBuilder addTrackingId(String trackingId)
        throws IllegalArgumentException {
      return super.addTrackingId(trackingId);
    }

    @Override
    public GenericParametersBuilder addUserId(String userId) throws IllegalArgumentException {
      return super.addUserId(userId);
    }

    @Override
    public GenericParametersBuilder addVersion() {
      return super.addVersion();
    }
  }
}
