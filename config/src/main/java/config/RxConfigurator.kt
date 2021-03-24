package config

/**
 * Represents configuration values related to RxJava
 */
object RxConfigurator {
  
  private var _networkDelay = 800L
  private var _requestTimeout = 150000L
  private var _retryCount = 3L
  
  /** Fake network delay to so that loading wouldn't flash */
  val networkDelay get() = _networkDelay
  
  /** Max request timeout */
  val requestTimeout get() = _requestTimeout
  
  /** Max retry count for observable */
  val retryCount get() = _retryCount
  
  fun configureNetworkDelay(networkDelay: Long) {
    _networkDelay = networkDelay
  }
  
  fun configureRequestTimeout(requestTimeout: Long) {
    _requestTimeout = requestTimeout
  }
  
  fun configureRetryCount(retryCount: Long) {
    _retryCount = retryCount
  }
}