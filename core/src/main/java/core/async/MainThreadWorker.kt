package core.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Future

class MainThreadWorker : Worker {
  
  private val mainThreadExecutor = object : Executor {
    private val handler = Handler(Looper.getMainLooper())
    
    override fun execute(command: Runnable) {
      handler.post(command)
    }
  }
  
  override fun submit(block: () -> Unit): Future<*>? {
    mainThreadExecutor.execute(block)
    return null
  }
  
}