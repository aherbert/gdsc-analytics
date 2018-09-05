package uk.ac.sussex.gdsc.analytics;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A dummy class implementing the {@link Future} interface to return a status result.
 */
public class DispatchFuture implements Future<DispatchStatus> {

  /** The status. */
  private final DispatchStatus status;

  /**
   * Instantiates a new dispatch future.
   *
   * @param status the status
   */
  DispatchFuture(DispatchStatus status) {
    this.status = status;
  }

  /**
   * Gets the status.
   *
   * @return the status
   */
  public DispatchStatus getStatus() {
    return status;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#cancel(boolean)
   */
  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#isCancelled()
   */
  @Override
  public boolean isCancelled() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#isDone()
   */
  @Override
  public boolean isDone() {
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#get()
   */
  @Override
  public DispatchStatus get() throws InterruptedException, ExecutionException {
    return getStatus();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
   */
  @Override
  public DispatchStatus get(long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return getStatus();
  }
}
