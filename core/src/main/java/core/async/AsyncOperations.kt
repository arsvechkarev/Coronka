package core.async

import core.Application.Threader
import core.releasable.Releasable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

class AsyncOperations(amount: Int, private val threader: Threader = Threader) : Releasable {
  
  private var operation: Future<*>? = null
  
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
    operation?.cancel(true)
  }
  
  fun onDoneAll(block: (Map<String, Any>) -> Unit) {
    if (isCancelled) return
    operation = threader.ioWorker.submit {
      latch.await()
      if (!isCancelled) {
        threader.mainThreadWorker.submit { block(operations) }
      }
    }
  }
  
  override fun release() {
    cancel()
  }
  
  private fun throwStateError() {
    throw IllegalStateException("AsyncOperations can be used only once")
  }
}