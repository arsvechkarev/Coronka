package core.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BackgroundWorker(
  private val executor: ExecutorService
) : Worker {
  
  override fun execute(block: () -> Unit) {
    executor.submit(block)
  }
  
  companion object {
    fun default(): BackgroundWorker {
      return BackgroundWorker(Executors.newSingleThreadExecutor())
    }
  }
}