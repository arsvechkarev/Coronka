package core.log

import android.util.Log
import com.arsvechkarev.core.BuildConfig

interface Loggable {
  val tag: String
}

fun Loggable.log(message: () -> String) {
  if (BuildConfig.DEBUG) {
    Log.d(tag, message())
  }
}

fun Loggable.log(throwable: Throwable, message: () -> String = { tag }) {
  if (BuildConfig.DEBUG) {
    Log.e(tag, message(), throwable)
  }
}