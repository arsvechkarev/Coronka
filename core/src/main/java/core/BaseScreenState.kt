package core

import com.arsvechkarev.core.R
import com.google.firebase.FirebaseNetworkException
import java.net.UnknownHostException
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
    is FirebaseNetworkException -> FailureReason.NO_CONNECTION
    else -> FailureReason.UNKNOWN
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
}