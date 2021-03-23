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

/**
 * Converter from [FailureReason] to String message that could be displayed on the screen
 */
interface FailureReasonToMessageConverter {
  
  /** Returns message for [reason] */
  fun getMessageForReason(reason: FailureReason): String
}

fun Throwable.toFailureReason() = when (this) {
  is TimeoutException -> FailureReason.TIMEOUT
  is UnknownHostException -> FailureReason.NO_CONNECTION
  else -> FailureReason.UNKNOWN
}
