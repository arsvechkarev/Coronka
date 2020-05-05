package core.concurrency

import core.releasable.Releasable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

/**
 * Enables to perform several operation concurrently
 */
class AsyncOperations(amount: Int, private val threader: Threader = AndroidThreader) : Releasable {
  
  private var resultOperation: Future<*>? = null
  
  private var isCancelled: Boolean = false
  private val operations = ConcurrentHashMap<String, Any>()
  
  private val latch = CountDownLatch(amount)
  
  fun addValue(key: String, value: Any) {
    latch.countDown()
    operations[key] = value
  }
  
  fun countDown() {
    latch.countDown()
  }
  
  fun cancel() {
    isCancelled = true
    resultOperation?.cancel(true)
  }
  
  fun onDoneAll(block: (Map<String, Any>) -> Unit) {
    if (isCancelled) return
    resultOperation = threader.onIoThread {
      latch.await()
      if (!isCancelled) {
        threader.onMainThread { block(operations) }
      }
    }
  }
  
  override fun release() {
    cancel()
  }
}