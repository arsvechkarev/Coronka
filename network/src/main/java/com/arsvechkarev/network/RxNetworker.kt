package com.arsvechkarev.network

import io.reactivex.Observable
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class RxNetworker {
  
  fun requestSingle(url: String): Single<String> = Single.create { emitter ->
    val result = requestBlocking(url)
    if (!emitter.isDisposed) {
      emitter.onSuccess(result)
    }
  }
  
  fun requestObservable(url: String) = Observable.fromCallable {
    requestBlocking(url)
  }
  
  fun requestBlocking(url: String): String {
    val stringBuilder = StringBuilder()
    val urlInstance = URL(url)
    BufferedReader(InputStreamReader(urlInstance.openStream())).use {
      while (true) {
        val line = it.readLine() ?: break
        stringBuilder.append(line)
      }
    }
    return stringBuilder.toString()
  }
}