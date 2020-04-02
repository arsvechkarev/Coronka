package com.arsvechkarev.network

import core.ApplicationConfig.Threader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

class AsyncOperations(amount: Int, private val threader: Threader = Threader) {
  
  private var operation: Future<*>? = null
  
  private var isDone: Boolean = false
  private val operations = ConcurrentHashMap<String, Any>()
  
  private val latch = CountDownLatch(amount)
  
  fun addValue(key: String, value: Any) {
    latch.countDown()
    operations[key] = value
  }
  
  fun onDoneAll(block: (Map<String, Any>) -> Unit) {
    if (isDone) {
      throwStateError()
    }
    operation = threader.ioWorker.submit {
      latch.await()
      threader.mainThreadWorker.submit { block(operations) }
    }
  }
  
  private fun throwStateError() {
    throw IllegalStateException("AsyncOperations can be used only once")
  }
}