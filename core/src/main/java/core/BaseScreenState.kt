package core

import java.net.UnknownHostException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

/**
 * Base screen state for every state in view model
 */
abstract class BaseScreenState

object Loading : BaseScreenState()

class Failure(val reason: FailureReason) : BaseScreenState() {
  
  enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  
  companion object {
    
    fun Throwable.asFailureReason() = when (this) {
      is TimeoutException -> FailureReason.TIMEOUT
      is UnknownHostException -> FailureReason.NO_CONNECTION
      is ExecutionException -> {
        when (cause) {
          is TimeoutException -> FailureReason.TIMEOUT
          is UnknownHostException -> FailureReason.NO_CONNECTION
          else -> FailureReason.UNKNOWN
        }
      }
      else -> FailureReason.UNKNOWN
    }
  }
}