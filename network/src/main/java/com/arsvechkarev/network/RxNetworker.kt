package com.arsvechkarev.network

import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class RxNetworker {
  
  fun performRequest(url: String): Single<String> = Single.create { emitter ->
    val stringBuilder = StringBuilder()
    val urlInstance = URL(url)
    BufferedReader(InputStreamReader(urlInstance.openStream())).use {
      while (true) {
        val line = it.readLine() ?: break
        stringBuilder.append(line)
      }
    }
    if (!emitter.isDisposed) {
      emitter.onSuccess(stringBuilder.toString())
    }
  }
}