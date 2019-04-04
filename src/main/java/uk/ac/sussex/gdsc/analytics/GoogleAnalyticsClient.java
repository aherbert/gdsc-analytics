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

package uk.ac.sussex.gdsc.analytics;

import uk.ac.sussex.gdsc.analytics.parameters.FormattedParameter;
import uk.ac.sussex.gdsc.analytics.parameters.HitType;
import uk.ac.sussex.gdsc.analytics.parameters.HitTypeParameter;
import uk.ac.sussex.gdsc.analytics.parameters.NoIndexTextParameter;
import uk.ac.sussex.gdsc.analytics.parameters.ParameterUtils;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.HitBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.ParametersBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.PartialBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.Parameters.RequiredBuilder;
import uk.ac.sussex.gdsc.analytics.parameters.ProtocolSpecification;
import uk.ac.sussex.gdsc.analytics.parameters.SessionControlParameter;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Send custom requests to Google Analytics using the <a href=
 * "https://developers.google.com/analytics/devguides/collection/protocol/v1/">Google Analytics
 * Measurement Protocol</a>.
 *
 * <p>The client uses an {@link ExecutorService} to send requests via a {@link HitDispatcher}.
 *
 * <p>The client represents interactions of a single user (or client). This requires a Google
 * Analytics tracking Id parameter and either a client or user Id parameter. These are fixed for a
 * single client instance.
 *
 * <p>The client manages the session of interaction with a configurable timeout.
 */
public class GoogleAnalyticsClient {

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

  /** The initial size for the buffer used for the hit string. */
  private static final int HIT_BUFFER_SIZE = 512;

  /**
   * The executor service for dispatching background requests.
   */
  private final ExecutorService executorService;

  /** The hit dispatcher. */
  private final HitDispatcher hitDispatcher;

  /** The client parameters. These are sent with each hit. */
  private final FormattedParameter clientParameters;

  /** The session parameters. These are sent with each new session. */
  private final FormattedParameter sessionParameters;

  /** The session. */
  private final Session session;

  /** The ignore flag. */
  private boolean ignore;

  /**
   * Builder to create {@link GoogleAnalyticsClient} instances.
   *
   * <p>This class can be used to create multiple instances with shared properties, e.g. the
   * tracking Id or the executor service.
   */
  public static final class Builder {

    /** Used to reset the hit dispatcher when URL settings change. */
    private static final HitDispatcher NO_HIT_DISPATCHER = null;

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

    /** The hit dispatcher. */
    private HitDispatcher hitDispatcher;

    /** The per-hit parameters. */
    private PartialBuilder<Builder> perHitParameters;

    /** The per-session parameters. */
    private PartialBuilder<Builder> perSessionParameters;

    /** The session timeout. */
    private long sessionTimeout = Session.DEFAULT_TIMEOUT;

    /** The secure flag. Set to true to use HTTPS. */
    private boolean secure;

    /** The debug flag. Set to true to use the Google Analytics debug server. */
    private boolean debug;

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
     */
    public GoogleAnalyticsClient build() {

      // This will work if user/client Id are null as it generates a random UUID
      final RequiredBuilder clientBuilder = Parameters.newRequiredBuilder(trackingId);
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

      // Fix the parameters
      final FormattedParameter clientParameters = clientBuilder.build();
      final FormattedParameter sessionParameters =
          (perSessionParameters == null) ? FormattedParameter.empty()
              : perSessionParameters.build();

      return new GoogleAnalyticsClient(clientParameters, sessionParameters, this);
    }

    /**
     * Sets the Google Analytics tracking id.
     *
     * <p>The format is UA-XXXX-Y.
     *
     * @param trackingId the tracking id
     * @return the builder
     * @throws IllegalArgumentException if tracking ID is invalid
     * @see <a href="http://goo.gl/a8d4RP#tid">Tracking Id</a>
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
     * @see <a href="http://goo.gl/a8d4RP#uid">User Id</a>
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
     * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
     */
    public Builder setClientId(String clientId) {
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
     * @see <a href="http://goo.gl/a8d4RP#cid">Client Id</a>
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
     * <p>If positive a fixed thread pool is used. Otherwise a cached thread pool is used.
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
    public Builder setThreadPriority(int priority) {
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
    public ExecutorService getOrCreateExecutorService() {
      ExecutorService es = executorService;
      if (es == null) {
        final ThreadFactory tf = new BackgroundThreadFactory(getThreadPriority());
        final int localThreadCount = getThreadCount();
        if (localThreadCount > 0) {
          es = Executors.newFixedThreadPool(localThreadCount, tf);
        } else {
          es = Executors.newCachedThreadPool(tf);
        }
        executorService = es;
      }
      return es;
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
     * Gets the hit dispatcher used to send hit requests.
     *
     * <p>If {@code null} then a default dispatcher will be created using the values for
     * {@link #isSecure()} and {@link #isDebug()}.
     *
     * @see DefaultHitDispatcher#getDefault(boolean, boolean)
     *
     * @return the hit dispatcher
     */
    public HitDispatcher getOrCreateHitDispatcher() {
      HitDispatcher hd = hitDispatcher;
      if (hd == null) {
        hd = DefaultHitDispatcher.getDefault(isSecure(), isDebug());
        hitDispatcher = hd;
      }
      return hd;
    }

    /**
     * Sets the hit dispatcher used to send hit requests. Defaults to {@code null}.
     *
     * <p>If {@code null} then a default dispatcher will be used.
     *
     * @param hitDispatcher the hit dispatcher
     * @return the builder
     * @see DefaultHitDispatcher#getDefault(boolean, boolean)
     */
    public Builder setHitDispatcher(HitDispatcher hitDispatcher) {
      this.hitDispatcher = hitDispatcher;
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
    public PartialBuilder<Builder> getOrCreatePerHitParameters() {
      PartialBuilder<Builder> builder = perHitParameters;
      if (builder == null) {
        builder = Parameters.newPartialBuilder(this);
        perHitParameters = builder;
      }
      return builder;
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
    public Builder setPerHitParameters(FormattedParameter perHitParameters) {
      final PartialBuilder<Builder> builder = Parameters.newPartialBuilder(this);
      builder.add(perHitParameters);
      this.perHitParameters = builder;
      return this;
    }

    /**
     * Gets the parameters that will be used for each new session or creates them if absent.
     *
     * @return the per-session parameters
     */
    public PartialBuilder<Builder> getOrCreatePerSessionParameters() {
      PartialBuilder<Builder> builder = perSessionParameters;
      if (builder == null) {
        builder = Parameters.newPartialBuilder(this);
        perSessionParameters = builder;
      }
      return builder;
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
    public Builder setPerSessionParameters(FormattedParameter perSessionParameters) {
      final PartialBuilder<Builder> builder = Parameters.newPartialBuilder(this);
      builder.add(perSessionParameters);
      this.perSessionParameters = builder;
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
    public Builder setSessionTimeout(long timeout) {
      this.sessionTimeout = ParameterUtils.requirePositive(timeout, "Timeout must be positive");
      return this;
    }

    /**
     * Checks if is using a secure connection (HTTPS).
     *
     * <p>This value is used to create a default hit dispatcher and is ignored if the hit dispatcher
     * is provided.
     *
     * @return true if is secure
     */
    public boolean isSecure() {
      return secure;
    }

    /**
     * Sets to true to use a secure connection (HTTPS).
     *
     * <p>This value is used to create a default hit dispatcher and is ignored if the hit dispatcher
     * is provided.
     *
     * <p>Note: Setting this value will set the hit dispatcher to null if the property value is
     * different.
     *
     * @param secure true for HTTPS
     * @return the builder
     * @see #getOrCreateHitDispatcher()
     */
    public Builder setSecure(boolean secure) {
      if (secure != this.secure) {
        hitDispatcher = NO_HIT_DISPATCHER;
      }
      this.secure = secure;
      return this;
    }

    /**
     * Check the debug flag. Set to true to use the Google Analytics debug server.
     *
     * <p>This value is used to create a default hit dispatcher and is ignored if the hit dispatcher
     * is provided.
     *
     * @return true, if is debug
     */
    public boolean isDebug() {
      return debug;
    }

    /**
     * Sets the debug flag. Set to true to use the Google Analytics debug server.
     *
     * <p>This value is used to create a default hit dispatcher and is ignored if the hit dispatcher
     * is provided.
     *
     * <p>Note: Setting this value will set the hit dispatcher to null if the property value is
     * different.
     *
     * @param debug the new debug
     * @return the builder
     * @see #getOrCreateHitDispatcher()
     */
    public Builder setDebug(boolean debug) {
      if (debug != this.debug) {
        hitDispatcher = NO_HIT_DISPATCHER;
      }
      this.debug = debug;
      return this;
    }
  }

  /**
   * Builder for a <strong>single</strong> Google Analytics hit.
   *
   * <p>This builder is coupled to the enclosing {@link GoogleAnalyticsClient} which provides client
   * parameters including the tracking Id and client/user id.
   */
  private final class GoogleAnalyticsHitBuilder extends HitBuilder<Future<DispatchStatus>> {

    /**
     * Creates a new hit builder.
     *
     * @param formattedParameter the formatted parameter
     * @param hitType the hit type
     * @param timestamp the timestamp
     */
    public GoogleAnalyticsHitBuilder(FormattedParameter formattedParameter,
        HitTypeParameter hitType, long timestamp) {
      super(formattedParameter, hitType, timestamp);
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
   * @param builder the builder
   */
  private GoogleAnalyticsClient(FormattedParameter clientParameters,
      FormattedParameter sessionParameters, Builder builder) {
    // Freeze the parameters
    this.clientParameters = Objects.requireNonNull(clientParameters, "Client parameters").freeze();
    this.sessionParameters = Objects.requireNonNull(sessionParameters, "Session parameters").freeze();
    executorService = builder.getOrCreateExecutorService();
    hitDispatcher = builder.getOrCreateHitDispatcher();
    session = new Session(builder.getSessionTimeout());
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
   * @see <a href="http://goo.gl/a8d4RP#tid">Tracking Id</a>
   */
  public static Builder newBuilder(String trackingId) {
    return new Builder(trackingId);
  }

  //@formatter:off
  /**
   * Create a Builder for a <strong>single</strong> Google Analytics hit.
   *
   * <p>This builder is coupled to the enclosing {@link GoogleAnalyticsClient} which
   * provides client parameters including the tracking Id and client/user id.
   *
   * <p>This method ensures the following occur:
   *
   * <ul>
   * <li>Refreshes the session
   * <li>Initialises the hit type
   * <li>Stores the hit timestamp
   * <li>If a new session, adds the session level parameters from the {@link GoogleAnalyticsClient}
   * <li>Allows any additional hit parameters to be added
   * <li>Provides a method to send the request
   * <li>The client parameters are added within the send method
   * </ul>
   *
   * <p>The user is responsible for ensuring all the required parameters for the hit are added.
   * These may be added to the hit builder or may already be part of the client or session
   * parameters.
   *
   * @param hitType the hit type
   * @return the hit builder
   */
  //@formatter:on
  HitBuilder<Future<DispatchStatus>> newHitBuilder(HitTypeParameter hitType) {
    final boolean isNew = session.refresh();
    final HitBuilder<Future<DispatchStatus>> builder =
        new GoogleAnalyticsHitBuilder(clientParameters, hitType, session.getTimeStamp());
    if (isNew) {
      builder.add(SessionControlParameter.START);
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
  public HitBuilder<Future<DispatchStatus>> hit(HitType hitType) {
    return newHitBuilder(HitTypeParameter.create(hitType));
  }

  /**
   * Creates the {@link HitBuilder} for a pageview hit.
   *
   * <p>This is a utility function to set the required parameter.
   *
   * @param documentLocationUrl the document location URL
   * @return the hit builder
   * @see ParametersBuilder#addDocumentLocationUrl(String)
   */
  public HitBuilder<Future<DispatchStatus>> pageview(String documentLocationUrl) {
    return newHitBuilder(HitTypeParameter.PAGEVIEW).addDocumentLocationUrl(documentLocationUrl);
  }

  /**
   * Creates the {@link HitBuilder} for a pageview hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * <p>The document path should begin with a '/' character.
   *
   * @param documentHostName the document host name
   * @param documentPath the document path
   * @return the hit builder
   * @see ParametersBuilder#addDocumentHostName(String)
   * @see ParametersBuilder#addDocumentPath(String)
   * @throws IllegalArgumentException if the path is not valid
   */
  public HitBuilder<Future<DispatchStatus>> pageview(String documentHostName, String documentPath) {
    return newHitBuilder(HitTypeParameter.PAGEVIEW).addDocumentHostName(documentHostName)
        .addDocumentPath(documentPath);
  }

  /**
   * Creates the {@link HitBuilder} for a screenview hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param screenName the screen name
   * @return the hit builder
   * @see ParametersBuilder#addScreenName(String)
   */
  public HitBuilder<Future<DispatchStatus>> screenview(String screenName) {
    return newHitBuilder(HitTypeParameter.SCREENVIEW).addScreenName(screenName);
  }

  /**
   * Creates the {@link HitBuilder} for an event hit.
   *
   * <p>This is a utility function to set the required parameters. The event label and/or value can
   * be set on the returned builder.
   *
   * @param eventCategory the event category
   * @param eventAction the event action
   * @return the hit builder
   * @see ParametersBuilder#addEventCategory(String)
   * @see ParametersBuilder#addEventAction(String)
   * @see ParametersBuilder#addEventLabel(String)
   * @see ParametersBuilder#addEventValue(int)
   */
  public HitBuilder<Future<DispatchStatus>> event(String eventCategory, String eventAction) {
    return newHitBuilder(HitTypeParameter.EVENT).addEventCategory(eventCategory)
        .addEventAction(eventAction);
  }

  /**
   * Creates the {@link HitBuilder} for a transaction hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param transactionId the transaction id
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> transaction(String transactionId) {
    return newHitBuilder(HitTypeParameter.TRANSACTION)
        .add(new NoIndexTextParameter(ProtocolSpecification.TRANSACTION_ID, transactionId));
  }

  /**
   * Creates the {@link HitBuilder} for a item hit.
   *
   * <p>This is a utility function to set the required parameters.
   *
   * @param transactionId the transaction id
   * @param itemName the item name
   * @return the hit builder
   */
  public HitBuilder<Future<DispatchStatus>> item(String transactionId, String itemName) {
    return newHitBuilder(HitTypeParameter.ITEM)
        .add(new NoIndexTextParameter(ProtocolSpecification.TRANSACTION_ID, transactionId))
        .add(new NoIndexTextParameter(ProtocolSpecification.ITEM_NAME, itemName));
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
    return newHitBuilder(HitTypeParameter.SOCIAL).addSocialNetwork(socialNetwork)
        .addSocialAction(socialAction).addSocialActionTarget(socialActionTarget);
  }

  /**
   * Creates the {@link HitBuilder} for an exception hit.
   *
   * <p>Optional parameters to be added are: exception description; is exception fatal.
   *
   * @return the hit builder
   * @see ParametersBuilder#addExceptionDescription(String)
   * @see ParametersBuilder#addIsExceptionFatal(boolean)
   */
  public HitBuilder<Future<DispatchStatus>> exception() {
    return newHitBuilder(HitTypeParameter.EXCEPTION);
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
   * @see ParametersBuilder#addUserTimingCategory(String)
   * @see ParametersBuilder#addUserTimingVariableName(String)
   * @see ParametersBuilder#addUserTimingTime(int)
   */
  public HitBuilder<Future<DispatchStatus>> timing(String userTimingCategory,
      String userTimingVariableName, int userTimingTime) {
    return newHitBuilder(HitTypeParameter.TIMING).addUserTimingCategory(userTimingCategory)
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
   * Return {@code true} if this tracker is disabled due to an error.
   *
   * @return true, if is disabled
   * @see HitDispatcher#isDisabled()
   */
  public boolean isDisabled() {
    return hitDispatcher.isDisabled();
  }

  /**
   * Reset the session (i.e. start a new session)
   */
  public void resetSession() {
    session.reset();
  }

  /**
   * Gets the executor service.
   *
   * <p>This can be used to permanently shutdown the client.
   *
   * <p>Warning: The service may be shared among client instances so use with care!
   *
   * <p>Fast on/off switching of the client can be done using {@link #setIgnore(boolean)}.
   *
   * @return the executor service
   * @see #setIgnore(boolean)
   */
  public ExecutorService getExecutorService() {
    return executorService;
  }

  /**
   * Gets the hit dispatcher.
   *
   * <p>This can be used to permanently disable the client.
   *
   * <p>Warning: The dispatcher may be shared among client instances so use with care!
   *
   * <p>Fast on/off switching of the client can be done using {@link #setIgnore(boolean)}.
   *
   * @return the hit dispatcher
   * @see #setIgnore(boolean)
   */
  public HitDispatcher getHitDispatcher() {
    return hitDispatcher;
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
    // Build the request
    final StringBuilder sb = new StringBuilder(HIT_BUFFER_SIZE);
    parameters.formatTo(sb);
    return hitDispatcher.send(sb, timestamp);
  }
}
