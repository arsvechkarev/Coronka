package core

import androidx.lifecycle.MutableLiveData
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

/**
 * Base screen state for every state in view model
 *
 * @property _isOld Because we now LiveData emits latest value when observer subscribes to
 * it, there is no way we can check whether live data callback was invoked because of user
 * interactions or because of fragment went through configuration change. This flag can help
 * determine whether state was genuinely changed by user, or this is an old state from previous
 * fragment. Just set this value to true on Fragment.onDestroy(), and false any other time and
 * you will be able to check it in fragment
 */
abstract class BaseScreenState {
  
  internal var _isOld: Boolean = false
  
  val isStateOld get() = _isOld
}

/**
 * Represents loading state
 */
object Loading : BaseScreenState()

/**
 * Represents failure state
 */
class Failure(val throwable: Throwable) : BaseScreenState() {
  
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

fun MutableLiveData<BaseScreenState>.markAsOld() {
  (this.value as BaseScreenState)._isOld = true
}