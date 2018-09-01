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

package uk.ac.sussex.gdsc.analytics;

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Common client data. Allows caching of the client component of the Google Analytics URL.
 *
 * @see <a href=
 *      "https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters">Measurement
 *      Protocol Parameter Reference</a>
 */
public class ClientParameters extends Parameters {

  /** The constant used for no URL. */
  private static final String NO_URL = null;

  /** The tracking id. */
  private final String trackingId;

  /** The client id. */
  private final String clientId;

  /** The application name. */
  private final String applicationName;

  /** The session. */
  private final Session session;

  /** The screen resolution. */
  private String screenResolution;

  /** The user language. */
  private String userLanguage;

  /** The host name. */
  private String hostName;

  /** The user agent. */
  private String userAgent;

  /** The application id. */
  private String applicationId;

  /** The application version. */
  private String applicationVersion;

  /** The anonymised flag. */
  private boolean anonymised;

  /** The url. */
  private String url;

  /**
   * Constructs with the tracking Id. If the client Id is null or empty then a new UUID will be
   * created.
   *
   * @param trackingId Tracking Id (must not be empty)
   * @param clientId Client Id (optional)
   * @param applicationName Application name (must not be empty)
   * @throws IllegalArgumentException If tracking Id or application name are empty.
   */
  public ClientParameters(String trackingId, String clientId, String applicationName)
      throws IllegalArgumentException {
    checkNotEmpty(trackingId, "Tracking code cannot be null");
    checkNotEmpty(applicationName, "Application name cannot be null");
    checkTrackingId(trackingId);
    this.clientId = getClientIdOrDefault(clientId);
    this.trackingId = trackingId;
    this.applicationName = applicationName;
    this.session = new Session();
  }

  /**
   * Check the string is not null or empty.
   *
   * @param string the string
   * @param message the message
   * @throws IllegalArgumentException If the string is null or empty
   */
  private static void checkNotEmpty(String string, String message) throws IllegalArgumentException {
    if (string == null || string.length() == 0) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Check the tracking id has a correct Google Analytics format, otherwise log a warning.
   *
   * @param trackingId the tracking id
   */
  private static void checkTrackingId(String trackingId) {
    if (!Pattern.matches("[A-Z]+-[0-9]+-[0-9]+", trackingId)) {
      Logger.getLogger(ClientParameters.class.getName())
          .warning("Tracking code appears invalid: " + trackingId);
    }
  }

  /**
   * Get the client id, or if null or empty, a default random UUID.
   *
   * @param clientId the client id
   * @return the client id
   */
  private static String getClientIdOrDefault(String clientId) {
    return (clientId == null || clientId.length() == 0) ? java.util.UUID.randomUUID().toString()
        : clientId;
  }

  /**
   * Gets the tracking id.
   *
   * @return the tracking Id
   */
  public String getTrackingId() {
    return trackingId;
  }

  /**
   * Gets the client id.
   *
   * @return The client Id
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Gets the application name.
   *
   * @return the application name
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * Check if this is a new session.
   *
   * @return True if the session is new
   */
  public boolean isNewSession() {
    return session.isNew();
  }

  /**
   * Gets the screen resolution.
   *
   * @return the screen resolution
   */
  public String getScreenResolution() {
    return screenResolution;
  }

  /**
   * Gets the user language.
   *
   * @return the user language
   */
  public String getUserLanguage() {
    return userLanguage;
  }

  /**
   * Gets the host name.
   *
   * @return The hostname
   */
  public String getHostName() {
    return hostName;
  }

  /**
   * Gets the user agent.
   *
   * @return the user agent
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * Gets the application id.
   *
   * @return the application Id
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * Gets the application version.
   *
   * @return the application version
   */
  public String getApplicationVersion() {
    return applicationVersion;
  }

  /**
   * Checks if is anonymised.
   *
   * @return True if the IP address of the sender will be anonymised
   */
  public boolean isAnonymised() {
    return anonymised;
  }

  /**
   * Gets the url.
   *
   * @return The client component of the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Reset the cached URL.
   */
  private void resetUrl() {
    this.url = NO_URL;
  }

  /**
   * Sets the screen resolution, like "1280x800".
   *
   * @param screenResolution the screen resolution to set
   */
  public void setScreenResolution(String screenResolution) {
    resetUrl();
    this.screenResolution = screenResolution;
  }

  /**
   * Sets the user language, like "EN-us".
   *
   * @param userLanguage the user language to set
   */
  public void setUserLanguage(String userLanguage) {
    resetUrl();
    this.userLanguage = userLanguage;
  }

  /**
   * Set the hostname.
   *
   * @param hostName the hostname
   */
  public void setHostName(String hostName) {
    resetUrl();
    this.hostName = hostName;
  }

  /**
   * Set the client component of the Google Analytics URL. This can be used to cache part of the
   * URL.
   *
   * @param url The client component of the URL
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Sets the user agent.
   *
   * @param userAgent the user agent to set
   */
  public void setUserAgent(String userAgent) {
    resetUrl();
    this.userAgent = userAgent;
  }

  /**
   * Sets the application id.
   *
   * @param applicationId the application Id to set
   */
  public void setApplicationId(String applicationId) {
    resetUrl();
    this.applicationId = applicationId;
  }

  /**
   * Sets the application version.
   *
   * @param applicationVersion the application version to set
   */
  public void setApplicationVersion(String applicationVersion) {
    resetUrl();
    this.applicationVersion = applicationVersion;
  }

  /**
   * Set the state of IP anonymisation.
   *
   * @param anonymised True if the IP address of the sender will be anonymised
   */
  public void setAnonymised(boolean anonymised) {
    resetUrl();
    this.anonymised = anonymised;
  }

  /**
   * Add a session level custom dimension. These will only be sent at the start of the session.
   *
   * @param index the index
   * @param value the value
   * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomDimension(int, java.lang.String)
   */
  @Override
  public void addCustomDimension(int index, String value) {
    resetUrl();
    super.addCustomDimension(index, value);
  }

  /**
   * Add a session level custom metric. These will only be sent at the start of the session.
   *
   * @param index the index
   * @param value the value
   * @see uk.ac.sussex.gdsc.analytics.Parameters#addCustomMetric(int, int)
   */
  @Override
  public void addCustomMetric(int index, int value) {
    resetUrl();
    super.addCustomMetric(index, value);
  }

  /**
   * Reset the session (i.e. start a new session)
   */
  public void resetSession() {
    this.session.reset();
  }
}
