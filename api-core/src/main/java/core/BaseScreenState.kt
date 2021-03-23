package core

import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Base screen state for every state in view model
 */
interface BaseScreenState

/**
 * Represents loading state
 */
object Loading : BaseScreenState

/**
 * Represents failure state
 */
class Failure(val throwable: Throwable) : BaseScreenState {
  
  val reason = throwable.toFailureReason()
}

enum class FailureReason {
  NO_CONNECTION,
  TIMEOUT,
  UNKNOWN;
}

fun Throwable.toFailureReason() = when (this) {
  is TimeoutException -> FailureReason.TIMEOUT
  is UnknownHostException -> FailureReason.NO_CONNECTION
  else -> FailureReason.UNKNOWN
}