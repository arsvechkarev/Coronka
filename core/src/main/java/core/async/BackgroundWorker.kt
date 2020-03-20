package core.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BackgroundWorker(
  private val executor: ExecutorService
) : Worker {
  
  override fun submit(block: () -> Unit) {
    executor.submit(block)
  }
  
  companion object {
    fun default(): BackgroundWorker {
      return BackgroundWorker(Executors.newSingleThreadExecutor())
    }
    
    fun io(): BackgroundWorker {
      return BackgroundWorker(Executors.newFixedThreadPool(5))
    }
  }
}