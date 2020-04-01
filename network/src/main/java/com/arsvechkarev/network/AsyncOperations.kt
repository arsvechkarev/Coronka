package com.arsvechkarev.network

import core.ApplicationConfig.Threader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

class AsyncOperations(private val amount: Int, private val threader: Threader = Threader) {
  
  private var operation: Future<*>? = null
  
  private var isCancelled: Boolean = false
  private var isDone: Boolean = false
  private val operations = ConcurrentHashMap<String, Any>()
  
  private val latch = CountDownLatch(amount)
  
  fun addValue(key: String, value: Any) {
    isCancelled = false
    if (isDone) {
      throwStateError()
    }
    latch.countDown()
    operations[key] = value
  }
  
  fun onDoneAll(block: (Map<String, Any>) -> Unit) {
    if (isDone) {
      throwStateError()
    }
    operation = threader.ioWorker.submit {
      latch.await()
      if (!isCancelled) {
        threader.mainThreadWorker.submit { block(operations) }
      }
      isDone = true
    }
  }
  
  private fun throwStateError() {
    throw IllegalStateException("AsyncOperations can be used only once")
  }
}