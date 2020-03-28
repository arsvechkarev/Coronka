package core

import android.util.Log
import com.arsvechkarev.core.BuildConfig

interface Loggable {
  val logTag: String
}

fun Loggable.log(message: () -> String) {
  if (BuildConfig.DEBUG) {
    Log.d(logTag, message())
  }
}

fun Loggable.log(throwable: Throwable, message: () -> String = { logTag }) {
  if (BuildConfig.DEBUG) {
    Log.e(logTag, message(), throwable)
  }
}