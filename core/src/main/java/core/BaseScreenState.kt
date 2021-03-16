package core

import com.arsvechkarev.core.R
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
 *
 */
class Failure(val throwable: Throwable) : BaseScreenState {
  
  val reason = throwable.toFailureReason()
}

enum class FailureReason {
  NO_CONNECTION,
  TIMEOUT,
  UNKNOWN;
  
  fun getStringRes() = when (this) {
    NO_CONNECTION -> R.string.error_no_connection
    TIMEOUT -> R.string.error_timeout
    UNKNOWN -> R.string.error_unknown
  }
}

fun Throwable.toFailureReason() = when (this) {
  is TimeoutException -> FailureReason.TIMEOUT
  is UnknownHostException -> FailureReason.NO_CONNECTION
  else -> FailureReason.UNKNOWN
}