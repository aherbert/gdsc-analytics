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

import uk.ac.sussex.gdsc.analytics.parameters.FormattedParameter;
import uk.ac.sussex.gdsc.analytics.parameters.HitTypeParameter;
import uk.ac.sussex.gdsc.analytics.parameters.ParameterUtils;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.ClientParametersBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.GenericParametersBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.HitBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.NonClientParametersBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.QueueTimeParameter;
import uk.ac.sussex.gdsc.analytics.parameters.SessionControlParameter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Send custom requests to Google Analytics using the <a href=
 * "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google Analytics
 * Measurement Protocol</a>.
 *
 * <p>The client uses an {@link ExecutorService} to send requests.
 *
 * <p>The client is represents interactions of a single user (or client). This requires a Google
 * Analytics tracking Id parameter and either a client or user Id parameter. These are fixed for a
 * single client instance.
 *
 * <p>The client manages the session of interaction with a configurable timeout.
 */
public class GoogleAnalyticsClient {

  // TODO -
  // Do not resend all of client URL every time.
  // Test against google debug server.
  // Move mock test to integration tests.

  /** The hostname for the Google Analytics URL. */
  public static final String GOOGLE_ANALYTICS_HOSTNAME = "www.google-analytics.com";
  /** The file for the Google Analytics debug collection. */
  public static final String GOOGLE_ANALYTICS_DEBUG_FILE = "/debug/collect";
  /** The file for the Google Analytics collection. */
  public static final String GOOGLE_ANALYTICS_FILE = "/collect";

  /** The logger. */
  private static final Logger logger = Logger.getLogger(GoogleAnalyticsClient.class.getName());

  /**
   * Used when ignoring requests due to {@link DispatchStatus#IGNORED}.
   */
  private static final DispatchFuture FUTURE_IGNORED = new DispatchFuture(DispatchStatus.IGNORED);
  /**
   * Used when ignoring requests due to {@link DispatchStatus#DISABLED}.
   */
  private static final DispatchFuture FUTURE_DISABLED = new DispatchFuture(DispatchStatus.DISABLED);
  /**
   * Used when ignoring requests due to {@link DispatchStatus#SHUTDOWN}.
   */
  private static final DispatchFuture FUTURE_SHUTDOWN = new DispatchFuture(DispatchStatus.SHUTDOWN);
  /**
   * The executor service for dispatching background requests.
   */
  private final ExecutorService executorService;

  /**
   * The last IO exception that occurred when dispatching a request. If this is not null then the
   * tracker is disabled as it is assumed that all subsequent tracking requests will fail.
   */
  private final AtomicReference<IOException> lastIoException = new AtomicReference<>();

  /**
   * The client parameters. These are sent with each hit.
   *
   * <p>Stored as a string that is used to initialise the hit {@code StringBuilder}.
   */
  private final String clientParameters;

  /** The session parameters. These are sent with each new session. */
  private final FormattedParameter sessionParameters;

  /** The session. */
  private final Session session;

  /** The url used for tracking requests. */
  private final URL url;

  /** The proxy used for tracking requests. */
  private final Proxy proxy;

  /** The connection provider. */
  private final HttpConnectionProvider connectionProvider;

  /** The ignore flag. */
  private boolean ignore;

  /**
   * Builder to create {@link GoogleAnalyticsClient} instances.
   *
   * <p>This class can be used to create multiple instances with shared properties, e.g. the
   * tracking Id or the executor service.
   */
  public static final class Builder {

    /** The tracking id. */
    private String trackingId;

    /** The user id. */
    private String userId;

    /** The client id. */
    private Object clientId;

    /** The thread count. */
    private int threadCount = 1;

    /** The priority. */
    private int threadPriority = Thread.MIN_PRIORITY;

    /** The executor service. */
    private ExecutorService executorService;

    /** The per-hit parameters. */
    private NonClientParametersBuilder perHitParameters;

    /** The per-session parameters. */
    private NonClientParametersBuilder perSessionParameters;

    /** The session timeout. */
    private long sessionTimeout;

    /** The proxy used for tracking requests. */
    private Proxy proxy;

    /** The secure flag. Set to true to use HTTPS. */
    private boolean secure;

    /** The debug flag. Set to true to use the Google Analytics debug server. */
    private boolean debug;

    /** The google analytics hostname. */
    private String googleAnalyticsHostname = GOOGLE_ANALYTICS_HOSTNAME;

    /** The google analytics file. */
    private String googleAnalyticsFile = GOOGLE_ANALYTICS_FILE;

    /** The connection provider. */
    private HttpConnectionProvider connectionProvider;

    /**
     * Creates a new builder.
     *
     * @param trackingId the tracking id
     */
    public Builder(String trackingId) {
      setTrackingId(trackingId);
    }

    /**
     * Builds the {@link GoogleAnalyticsClient}.
     *
     * @return the google analytics client
     * @throws MalformedUrlRuntimeException If the URL was malformed
     */
    public GoogleAnalyticsClient build() throws MalformedUrlRuntimeException {

      // This will work if user/client Id are null as it generates a random UUID
      final ClientParametersBuilder clientBuilder =
          Parameters.newClientParametersBuilder(trackingId);
      if (userId != null) {
        clientBuilder.addUserId(userId);
      }
      if (clientId != null) {
        if (clientId instanceof UUID) {
          clientBuilder.addClientId((UUID) clientId);
        } else {
          clientBuilder.addClientId((String) clientId);
        }
      }
      if (perHitParameters != null) {
        clientBuilder.add(perHitParameters.build());
      }
      final FormattedParameter sessionParameters =
          (perSessionParameters == null) ? FormattedParameter.empty()
              : perSessionParameters.build();

      // Set up the connection. This may be set for testing purposes.
      if (connectionProvider == null) {
        connectionProvider = new HttpConnectionProvider() {
          // The default method will be used
        };
      }

      return new GoogleAnalyticsClient(clientBuilder.build(), sessionParameters,
          createExecutorService(), sessionTimeout, getUrl(), proxy, connectionProvider);
    }

    /**
     * Creates the executor service.
     *
     * @return the executor service
     */
    private ExecutorService createExecutorService() {
      if (executorService == null) {
        final ThreadFactory tf = new BackgroundThreadFactory(threadPriority);
        if (threadCount <= 0) {
          executorService = Executors.newCachedThreadPool(tf);
        } else {
          executorService = Executors.newFixedThreadPool(threadCount);
        }
      }
      return executorService;
    }

    /**
     * Sets the Google Analytics tracking id.
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
    public Builder setTrackingId(String trackingId) {
      ParameterUtils.validateTrackingId(trackingId);
      this.trackingId = trackingId;
      return this;
    }

    /**
     * Gets the tracking id.
     *
     * @return the tracking id
     */
    public String getTrackingId() {
      return trackingId;
    }

    /**
     * Sets the user id.
     *
     * <p>This field is required if Client ID (cid) is not specified in the request.
     *
     * <p>It must not itself be PII (personally identifiable information).
     *
     * @param userId the user id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#uid">User
     *      Id</a>
     */
    public Builder setUserId(String userId) {
      this.userId = userId;
      return this;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
      return userId;
    }

    /**
     * Sets the client id.
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
    public Builder setClientId(String clientId) throws IllegalArgumentException {
      this.clientId = clientId;
      return this;
    }

    /**
     * Sets the client id.
     *
     * <p>This field is required if User ID (uid) is not specified in the request.
     *
     * <p>The value of this field should be a random UUID (version 4) as described in <a
     * href="http://www.ietf.org/rfc/rfc4122.txt">RFC 4122</a>.
     *
     * @param clientId the client id
     * @return the builder
     * @see <a
     *      href="https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters#cid">Client
     *      Id</a>
     */
    public Builder setClientId(UUID clientId) {
      this.clientId = clientId;
      return this;
    }

    /**
     * Gets the client id.
     *
     * @return the client id
     */
    public Object getClientId() {
      return (clientId == null) ? null : String.valueOf(clientId);
    }

    /**
     * Gets the thread count for the default executor service.
     *
     * @return the thread count
     */
    public int getThreadCount() {
      return threadCount;
    }

    /**
     * Sets the thread count for the default executor service.
     *
     * <p>If set to zero then a cached thread pool is used. Otherwise a fixed thread pool is used.
     *
     * <p>This is ignored if the executor service is provided.
     *
     * @param threadCount the new thread count
     * @return the builder
     * @see Executors#newCachedThreadPool()
     * @see Executors#newFixedThreadPool(int)
     * @see #setExecutorService(ExecutorService)
     */
    public Builder setThreadCount(int threadCount) {
      this.threadCount = threadCount;
      return this;
    }

    /**
     * Gets the thread priority for the default executor service.
     *
     * @return the priority
     */
    public int getThreadPriority() {
      return threadPriority;
    }

    /**
     * Sets the thread priority for the default executor service.
     *
     * <p>This is ignored if the executor service is provided.
     *
     * @param priority the new priority
     * @return the builder
     * @throws IllegalArgumentException If the priority is not in the allowed range for a thread
     */
    public Builder setThreadPriority(int priority) throws IllegalArgumentException {
      ArgumentUtils.validateThreadPriority(priority);
      this.threadPriority = priority;
      return this;
    }

    /**
     * Gets the executor service used by the client to send requests.
     *
     * <p>If {@code null} then a default executor service will be created using the thread count and
     * thread priority within {@link #build()}.
     *
     * @return the executor service
     * @see #setThreadCount(int)
     * @see #setThreadPriority(int)
     */
    public ExecutorService getExecutorService() {
      return executorService;
    }

    /**
     * Sets the executor service. Defaults to {@code null}.
     *
     * <p>Note: The executor service can be shared by client instances allowing all clients to be
     * simultaneously shutdown.
     *
     * <p>If a custom service is used the caller is responsible for the shutdown of the service.
     *
     * <p>If {@code null} then a default executor service will be created using the thread count and
     * thread priority and a factory that generates Daemon threads. This occurs within
     * {@link #build()}. The default is reused in subsequent {@link #build()} calls unless
     * explicitly set to {@code null}.
     *
     * <p>The default is unique to the builder, i.e. a new builder will create a new default
     * service. To share the service among clients requires using the same builder, or passing the
     * same service to a new builder.
     *
     * @param executorService the new executor service
     * @return the builder
     * @see #setThreadCount(int)
     * @see #setThreadPriority(int)
     */
    public Builder setExecutorService(ExecutorService executorService) {
      this.executorService = executorService;
      return this;
    }

    /**
     * Gets the parameters that will be used for each hit or creates them if absent.
     *
     * <p>These parameters are in addition to the required tracking Id and client/user Id which are
     * set using properties.
     *
     * @return the per-hit parameters
     */
    public NonClientParametersBuilder getOrCreatePerHitParameters() {
      if (perHitParameters == null) {
        perHitParameters = Parameters.newNonClientParametersBuilder();
      }
      return perHitParameters;
    }

    /**
     * Sets the parameters added to each hit.
     *
     * <p>These parameters are in addition to the required tracking Id and client/user Id which are
     * set using properties.
     *
     * <p>Note that the builder creates a client with a fixed view of the parameters. Subsequent
     * changes to the parameters will not effect existing client instances.
     *
     * @param perHitParameters the new per-hit parameters
     * @return the builder
     */
    public Builder setPerHitParameters(NonClientParametersBuilder perHitParameters) {
      this.perHitParameters = perHitParameters;
      return this;
    }

    /**
     * Gets the parameters that will be used for each new session or creates them if absent.
     *
     * @return the per-session parameters
     */
    public NonClientParametersBuilder getOrCreatePerSessionParameters() {
      if (perSessionParameters == null) {
        perSessionParameters = Parameters.newNonClientParametersBuilder();
      }
      return perSessionParameters;
    }

    /**
     * Sets the parameters added to each hit that occurs with a new session.
     *
     * <p>Note that the builder creates a client with a fixed view of the parameters. Subsequent
     * changes to the parameters will not effect existing client instances.
     *
     * @param perSessionParameters the new per-session parameters
     * @return the builder
     */
    public Builder setPerSessionParameters(NonClientParametersBuilder perSessionParameters) {
      this.perSessionParameters = perSessionParameters;
      return this;
    }

    /**
     * Gets the session timeout in milliseconds.
     *
     * @return the timeout
     */
    public long getSessionTimeout() {
      return sessionTimeout;
    }

    /**
     * Sets the session timeout in milliseconds.
     *
     * <p>Set to zero to ensure the session is always new (i.e. continuously timeout).
     *
     * @param timeout the timeout to set
     * @return the builder
     * @throws IllegalArgumentException If the timeout is negative
     */
    public Builder setSessionTimeout(long timeout) throws IllegalArgumentException {
      this.sessionTimeout = ParameterUtils.requirePositive(timeout, "Timeout must be positive");
      return this;
    }

    /**
     * Checks if is using a secure connection (HTTPS).
     *
     * @return true if is secure
     */
    public boolean isSecure() {
      return secure;
    }

    /**
     * Sets to true to use a secure connection (HTTPS).
     *
     * @param secure true for HTTPS
     * @return the builder
     */
    public Builder setSecure(boolean secure) {
      this.secure = secure;
      return this;
    }

    /**
     * Gets the proxy to use for all tracking requests.
     *
     * @return the proxy
     */
    public Proxy getProxy() {
      return proxy;
    }

    /**
     * Define the proxy to use for all tracking requests. You can pass {@link Proxy#NO_PROXY} to
     * explicitly use no proxy. Pass null to revert to the system default mechanism for connecting.
     *
     * @param proxy The proxy to use
     * @return the builder
     */
    public Builder setProxy(Proxy proxy) {
      this.proxy = proxy;
      return this;
    }

    /**
     * Define the proxy to use for all tracking requests.
     *
     * <p>If no proxy can be set then this will revert the system to default mechanism for
     * connecting.
     *
     * @param proxyAddress "hostname:port" of the proxy to use; may also be given as URL
     *        ("http://hostname:port/").
     * @return true, if successful
     */
    public boolean setProxy(String proxyAddress) {
      if (proxyAddress != null) {
        // Split into "hostname:port"
        final Matcher m = Pattern.compile("^(https?://|)([^ :]+):([0-9]+)").matcher(proxyAddress);
        if (m.find()) {
          final String hostname = m.group(2);
          final int port = Integer.parseInt(m.group(3));

          final SocketAddress sa = new InetSocketAddress(hostname, port);
          setProxy(new Proxy(Type.HTTP, sa));
          return true;
        }
      }
      setProxy((Proxy) null);
      return false;
    }

    /**
     * Sets the connection provider.
     *
     * <p>Used for testing.
     *
     * @param connectionProvider the new connection provider
     */
    void setConnectionProvider(HttpConnectionProvider connectionProvider) {
      this.connectionProvider = connectionProvider;
    }

    /**
     * Check the debug flag. Set to true to use the Google Analytics debug server.
     *
     * @return true, if is debug
     */
    public boolean isDebug() {
      return debug;
    }

    /**
     * Sets the debug flag. Set to true to use the Google Analytics debug server.
     *
     * @param debug the new debug
     * @return the builder
     */
    public Builder setDebug(boolean debug) {
      this.debug = debug;
      setFile((debug) ? GOOGLE_ANALYTICS_DEBUG_FILE : GOOGLE_ANALYTICS_FILE);
      return this;
    }

    /**
     * Gets the Google Analytics hostname component of the URL.
     *
     * @return the hostname
     */
    public String getHostname() {
      return googleAnalyticsHostname;
    }

    /**
     * Sets the Google Analytics hostname component of the URL.
     *
     * @param hostname the new hostname
     * @return the builder
     */
    public Builder setHostname(String hostname) {
      this.googleAnalyticsHostname = hostname;
      return this;
    }

    /**
     * Gets the Google Analytics file component of the URL.
     *
     * @return the file
     */
    public String getFile() {
      return googleAnalyticsFile;
    }

    /**
     * Sets the Google Analytics file component of the URL.
     *
     * @param file the new file
     * @return the builder
     */
    public Builder setFile(String file) {
      this.googleAnalyticsFile = file;
      return this;
    }

    /**
     * Gets the Google Analytics url.
     *
     * <p>This may throw a wrapped {@link MalformedURLException} if the hostname and file have been
     * changed from the defaults.
     *
     * @return the url
     * @throws MalformedUrlRuntimeException If the URL was malformed
     */
    public URL getUrl() throws MalformedUrlRuntimeException {
      try {
        final String protocol = (secure) ? "https" : "http";
        return new URL(protocol, getHostname(), getFile());
      } catch (final MalformedURLException ex) {
        logger.severe("Failed to create Google Analytics URL: " + ex.getMessage());
        throw new MalformedUrlRuntimeException(ex);
      }
    }
  }


  //@formatter:off
  /**
   * Builder for a <strong>single</strong> Google Analytics hit.
   *
   * <p>This builder is coupled to the enclosing {@link GoogleAnalyticsClient} which
   * provides client parameters including the tracking Id and client/user id.
   *
   * <p>The builder performs the following functions:
   *
   * <ul>
   * <li>Adds the client parameters from the {@link GoogleAnalyticsClient}
   * <li>Initialises the hit type
   * <li>Refreshes the session
   * <li>Stores the hit timestamp
   * <li>If a new session, adds the session level parameters from the {@link GoogleAnalyticsClient}
   * <li>Allows any additional hit parameters to be added
   * <li>Provides a method to send the request
   * </ul>
   *
   * <p>The user is responsible for ensuring all the required parameters for the hit are added.
   * These may be added to the hit builder or may already be part of the client or session
   * parameters.
   */
  //@formatter:on
  private final class GoogleAnalyticsHitBuilder extends HitBuilder<Future<DispatchStatus>> {

    /**
     * Creates a new hit builder.
     *
     * @param hitType the hit type
     * @param timestamp the timestamp
     */
    private GoogleAnalyticsHitBuilder(HitTypeParameter hitType, long timestamp) {
      super(hitType, timestamp);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.sussex.gdsc.analytics.parameters.Parameters.HitBuilder#send()
     */
    @Override
    public Future<DispatchStatus> send() {
      return GoogleAnalyticsClient.this.send(build(), getTimestamp());
    }
  }

  /**
   * Create an instance.
   *
   * @param clientParameters The client parameters (sent with every hit)
   * @param sessionParameters The session parameters (resent with each new session)
   * @param executorService The executor service for dispatching background requests
   * @param timeout the timeout
   * @param url the url
   * @param proxy the proxy
   * @param connectionProvider the connection provider
   */
  GoogleAnalyticsClient(FormattedParameter clientParameters, FormattedParameter sessionParameters,
      ExecutorService executorService, long timeout, URL url, Proxy proxy,
      HttpConnectionProvider connectionProvider) {
    Objects.requireNonNull(clientParameters, "Client parameters");
    Objects.requireNonNull(sessionParameters, "Session parameters");
    this.executorService = Objects.requireNonNull(executorService, "Executor service");
    // Generate state
    this.clientParameters = clientParameters.formatPostString();
    this.sessionParameters = sessionParameters.simplify();
    session = new Session(timeout);
    this.url = url;
    this.proxy = proxy;
    this.connectionProvider = connectionProvider;
  }

  /**
   * Creates a builder to create {@link GoogleAnalyticsClient} instances using the Google Analytics
   * tracking id.
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
  public static Builder createBuilder(String trackingId) {
    return new Builder(trackingId);
  }

  /**
   * Creates the hit builder.
   *
   * @param hitType the hit type
   * @return the hit builder
   */
  HitBuilder<Future<DispatchStatus>> createHitBuilder(HitTypeParameter hitType) {
    final boolean isNew = session.refresh();
    final HitBuilder<Future<DispatchStatus>> builder =
        new GoogleAnalyticsHitBuilder(hitType, session.getTimeStamp());
    if (isNew) {
      builder.addSessionControl(SessionControlParameter.START);
      builder.add(sessionParameters);
    }
    return builder;
  }

  /**
   * Creates the {@link HitBuilder} for the given hit type.
   *
   * @param hitType the hit type
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> hit(HitTypeParameter hitType) {
    Objects.requireNonNull(hitType);
    return createHitBuilder(hitType);
  }

  /**
   * Creates the {@link HitBuilder} for a pageview hit.
   *
   * <p>This is a utility function to set the required parameter.
   *
   * @param documentLocationUrl the document location URL
   * @return the hit builder
   * @see GenericParametersBuilder#addDocumentLocationUrl(String)
   */
  public HitBuilder<Future<DispatchStatus>> pageview(String documentLocationUrl) {
    return createHitBuilder(HitTypeParameter.PAGEVIEW).addDocumentLocationUrl(documentLocationUrl);
  }

  /**
   * Creates the {@link HitBuilder} for a pageview hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param documentHostName the document host name
   * @param documentPath the document path
   * @return the hit builder
   * @see GenericParametersBuilder#addDocumentHostName(String)
   * @see GenericParametersBuilder#addDocumentPath(String)
   */
  public HitBuilder<Future<DispatchStatus>> pageview(String documentHostName, String documentPath) {
    return createHitBuilder(HitTypeParameter.PAGEVIEW).addDocumentHostName(documentHostName)
        .addDocumentPath(documentPath);
  }

  /**
   * Creates the {@link HitBuilder} for a screenview hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param screenName the screen name
   * @return the hit builder
   * @see GenericParametersBuilder#addScreenName(String)
   */
  public HitBuilder<Future<DispatchStatus>> screenview(String screenName) {
    return createHitBuilder(HitTypeParameter.SCREENVIEW).addScreenName(screenName);
  }

  /**
   * Creates the {@link HitBuilder} for an event hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param eventCategory the event category
   * @param eventAction the event action
   * @param eventValue the event value
   * @return the hit builder
   * @see GenericParametersBuilder#addEventCategory(String)
   * @see GenericParametersBuilder#addEventAction(String)
   * @see GenericParametersBuilder#addEventValue(int)
   */
  public HitBuilder<Future<DispatchStatus>> event(String eventCategory, String eventAction,
      int eventValue) {
    return createHitBuilder(HitTypeParameter.EVENT).addEventCategory(eventCategory)
        .addEventAction(eventAction).addEventValue(eventValue);
  }

  /**
   * Creates the {@link HitBuilder} for a transaction hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param transactionId the transaction id
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> transaction(int transactionId) {
    // TODO
    return createHitBuilder(HitTypeParameter.TRANSACTION);
  }

  /**
   * Creates the {@link HitBuilder} for a item hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param transactionId the transaction id
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> item(int transactionId) {
    // TODO
    return createHitBuilder(HitTypeParameter.ITEM);
  }

  /**
   * Creates the {@link HitBuilder} for a social hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param socialNetwork the social network
   * @param socialAction the social action
   * @param socialActionTarget the social action target
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> social(String socialNetwork, String socialAction,
      String socialActionTarget) {
    // TODO
    return createHitBuilder(HitTypeParameter.SOCIAL);
  }

  /**
   * Creates the {@link HitBuilder} for an exception hit.
   *
   * <p>Optional parameters to be added are: exception description; is exception fatal.
   *
   * @return the hit builder
   * @see GenericParametersBuilder#addExceptionDescription(String)
   * @see GenericParametersBuilder#addIsExceptionFatal(boolean)
   */
  public HitBuilder<Future<DispatchStatus>> exception() {
    return createHitBuilder(HitTypeParameter.EXCEPTION);
  }

  /**
   * Creates the {@link HitBuilder} for a timing hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param userTimingCategory the user timing category
   * @param userTimingVariableName the user timing variable name
   * @param userTimingTime the user timing time
   * @return the hit builder
   * @see GenericParametersBuilder#addUserTimingCategory(String)
   * @see GenericParametersBuilder#addUserTimingVariableName(String)
   * @see GenericParametersBuilder#addUserTimingTime(int)
   */
  public HitBuilder<Future<DispatchStatus>> timing(String userTimingCategory,
      String userTimingVariableName, int userTimingTime) {
    return createHitBuilder(HitTypeParameter.TIMING).addUserTimingCategory(userTimingCategory)
        .addUserTimingVariableName(userTimingVariableName).addUserTimingTime(userTimingTime);
  }

  /**
   * Check if ignoring tracking requests.
   *
   * @return true if ignoring tracking requests
   */
  public boolean isIgnore() {
    return ignore;
  }

  /**
   * Sets to true to ignore tracking requests.
   *
   * @param ignore true if ignoring tracking requests
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * Returns {@code true} if this tracker has been shut down.
   *
   * @return {@code true} if this tracker has been shut down
   * @see ExecutorService#isShutdown()
   */
  public boolean isShutdown() {
    return executorService.isShutdown();
  }


  /**
   * Returns {@code true} if all tasks have completed following shut down. Note that
   * {@code isTerminated} is never {@code true} unless either {@code shutdown} or
   * {@code shutdownNow} was called first.
   *
   * @return {@code true} if all tasks have completed following shut down
   * @see ExecutorService#isTerminated()
   */
  public boolean isTerminated() {
    return executorService.isTerminated();
  }

  /**
   * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new
   * tasks will be accepted. Invocation has no additional effect if already shut down.
   *
   * <p>This method does not wait for previously submitted tasks to complete execution. Use
   * {@link #awaitTermination awaitTermination} to do that.
   *
   * @see ExecutorService#shutdown()
   */
  public void shutdown() {
    executorService.shutdown();
  }

  /**
   * Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and
   * returns a count of the tasks that were awaiting execution.
   *
   * <p>This method does not wait for actively executing tasks to terminate. Use
   * {@link #awaitTermination awaitTermination} to do that.
   *
   * <p>There are no guarantees beyond best-effort attempts to stop processing actively executing
   * tasks.
   *
   * @return a count of the tasks that were awaiting execution
   * @see ExecutorService#shutdownNow()
   */
  public int shutdownNow() {
    return executorService.shutdownNow().size();
  }

  /**
   * Blocks until all tasks have completed execution after a shutdown request, or the timeout
   * occurs, or the current thread is interrupted, whichever happens first.
   *
   * @param timeout the maximum time to wait
   * @return {@code true} if this executor terminated and {@code false} if the timeout elapsed
   *         before termination
   * @throws InterruptedException if interrupted while waiting
   * @see ExecutorService#awaitTermination(long, TimeUnit)
   */
  public boolean awaitTermination(long timeout) throws InterruptedException {
    return executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
  }

  /**
   * Send the tracking request to Google Analytics.
   *
   * <p>Note that if the current state of the tracker prevents sending the request then a dummy
   * future will be returned immediately.
   *
   * @param parameters The request parameters
   * @param timestamp The timestamp when the hit was reported (in milliseconds)
   * @return a Future representing pending completion of the task
   * @throws NullPointerException if request parameters are null
   */
  public Future<DispatchStatus> send(final Parameters parameters, final long timestamp) {
    if (isIgnore()) {
      return FUTURE_IGNORED;
    }
    if (isDisabled()) {
      return FUTURE_DISABLED;
    }
    if (isShutdown()) {
      return FUTURE_SHUTDOWN;
    }
    Objects.requireNonNull(parameters, "Request parameters cannot be null");
    return executorService.submit(() -> dispatchRequest(parameters, timestamp));
  }

  /**
   * Send the parameters to Google Analytics using the Measurement Protocol. This uses the HTTP POST
   * method.
   *
   * @param parameters The request parameter data
   * @param timestamp The timestamp when the hit was reported (in milliseconds)
   * @return true, if successful
   */
  private DispatchStatus dispatchRequest(Parameters parameters, long timestamp) {
    // Check this again as it may occur after the request was queued
    if (isDisabled()) {
      return DispatchStatus.DISABLED;
    }
    HttpURLConnection connection = null;
    try {
      connection = connectionProvider.openConnection(url, proxy);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded; charset=UTF-8");
      // Build the request
      final StringBuilder sb = new StringBuilder(clientParameters);
      parameters.appendTo(sb);
      // Add the queue time offset
      QueueTimeParameter.appendTo(sb, timestamp);
      // Send the request
      final byte[] out = sb.toString().getBytes(StandardCharsets.UTF_8);
      final int length = out.length;
      connection.setFixedLengthStreamingMode(length);
      connection.connect();
      try (OutputStream os = connection.getOutputStream()) {
        os.write(out);
      }
      final int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        logger.log(Level.FINE, () -> String.format("Tracking success for url '%s'", parameters));
        // This is a success. All other returns are false.
        return DispatchStatus.COMPLETE;
      }
      // Note: Valid on 31-Aug-2018
      // https://developers.google.com/analytics/devguides/collection/protocol/v1/validating-hits
      // "The Google Analytics Measurement Protocol does not return HTTP error codes"
      // This is unlikely to happen so log a warning but don't disable the tracker.
      // If Google change their response in the future this logging will serve
      // as notice to update the code to do something more appropriate.
      logger.log(Level.WARNING,
          () -> String.format("Error requesting url '%s', received response code %d", parameters,
              responseCode));
    } catch (

    final UnknownHostException ex) {
      setLastIoException(ex);
      // Occurs when disconnected from the Internet so this is not severe
      logger.log(Level.WARNING, () -> String.format("Unknown host: %s", ex.getMessage()));
    } catch (final IOException ex) {
      setLastIoException(ex);
      // Log all others at a severe level
      logger.log(Level.SEVERE, () -> String.format("Error making tracking request: %s : %s",
          ex.getClass().getSimpleName(), ex.getMessage()));
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return DispatchStatus.ERROR;
  }

  /**
   * Sets the last IO exception.
   *
   * <p>This will disable all tracking requests.
   *
   * @param ex the last IO exception
   */
  private void setLastIoException(IOException ex) {
    lastIoException.set(ex);
  }

  /**
   * Gets the last IO exception that occurred from a dispatch request.
   *
   * <p>If this is not {@code null} then all tracking is disabled as it is assumed that all
   * subsequent tracking requests will fail.
   *
   * @return the last IO exception
   */
  public IOException getLastIoException() {
    return lastIoException.get();
  }

  /**
   * Checks if the tracker is disabled due to an error.
   *
   * <p>This is true if an exception occurred during a dispatch request, i.e. any subsequent
   * tracking requests will fail.
   *
   * <p>The state can be reset using {@link #clearLastIoException()} if another attempt at tracking
   * is to be attempted, e.g. if the reason for the problem has been fixed.
   *
   * @return true, if is disabled
   * @see #getLastIoException()
   * @see #clearLastIoException()
   */
  public boolean isDisabled() {
    return lastIoException.get() != null;
  }

  /**
   * Clear the last IO exception.
   *
   * <p>Use this method to restart tracking.
   */
  public void clearLastIoException() {
    setLastIoException(null);
  }

  /**
   * Reset the session (i.e. start a new session)
   */
  public void resetSession() {
    session.reset();
  }
}
