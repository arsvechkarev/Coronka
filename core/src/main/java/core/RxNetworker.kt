package core

import com.arsvechkarev.core.BuildConfig
import io.reactivex.Observable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.TimeoutException
import kotlin.random.Random

class RxNetworker {
  
  fun requestObservable(url: String) = Observable.create<String> { emitter ->
    try {
      if (BuildConfig.DEBUG) {
        if (Random.nextBoolean()) {
          throw if (Random.nextBoolean()) TimeoutException() else ArrayStoreException()
        }
      }
      val stringBuilder = StringBuilder()
      val urlInstance = URL(url)
      BufferedReader(InputStreamReader(urlInstance.openStream())).use {
        while (true) {
          val line = it.readLine() ?: break
          stringBuilder.append(line)
        }
      }
      if (!emitter.isDisposed) {
        emitter.onNext(stringBuilder.toString())
      }
      emitter.onComplete()
    } catch (e: Throwable) {
      if (!emitter.isDisposed) {
        emitter.onError(e)
      }
    }
  }
}