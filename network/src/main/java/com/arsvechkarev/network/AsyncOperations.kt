package com.arsvechkarev.network

import core.ApplicationConfig.Threader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch

class AsyncOperations(amount: Int, private val threader: Threader = Threader) {
  
  private val operations = ConcurrentHashMap<String, Any>()
  
  private val latch = CountDownLatch(amount)
  
  fun addValue(key: String, value: Any) {
    latch.countDown()
    operations[key] = value
  }
  
  fun onDoneAll(block: (Map<String, Any>) -> Unit) {
    threader.ioWorker.submit {
      latch.await()
      threader.mainThreadWorker.submit { block(operations) }
    }
  }
}