package uk.ac.sussex.gdsc.analytics;

/**
 * The dispatch status.
 */
public enum DispatchStatus {
  /**
   * The request has been processed.
   */
  COMPLETE,
  /**
   * The request has errored during processing.
   */
  ERROR,
  /**
   * The request not been processed because the tracker is set to ignore.
   */
  IGNORED,
  /**
   * The request not been processed because the tracker is shutdown.
   */
  SHUTDOWN,
  /**
   * The request not been processed because the tracker previously errored and disabled.
   */
  DISABLED;
}
