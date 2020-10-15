package core

import java.net.UnknownHostException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

/**
 * Base screen state for every state in view model
 */
abstract class BaseScreenState

open class Loading : BaseScreenState()

open class Failure(
  val throwable: Throwable
) : BaseScreenState() {
  
  val reason: FailureReason = when (throwable) {
    is TimeoutException -> FailureReason.TIMEOUT
    is UnknownHostException -> FailureReason.NO_CONNECTION
    is ExecutionException -> {
      when (throwable.cause) {
        is TimeoutException -> FailureReason.TIMEOUT
        is UnknownHostException -> FailureReason.NO_CONNECTION
        else -> FailureReason.UNKNOWN
      }
    }
    else -> FailureReason.UNKNOWN
  }
  
  enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
}