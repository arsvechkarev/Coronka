package core.log

import android.util.Log
import com.arsvechkarev.core.BuildConfig
import java.lang.Exception

interface Loggable {
  val tag: String
}

fun Loggable.debug(message: () -> String) {
  if (BuildConfig.DEBUG) {
    Log.d(tag, message())
  }
}

fun Loggable.debug(exception: Exception, message: () -> String) {
  if (BuildConfig.DEBUG) {
    Log.e(tag, message(), exception)
  }
}