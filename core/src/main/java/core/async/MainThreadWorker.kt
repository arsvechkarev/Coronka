package core.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class MainThreadWorker : Worker {
  
  private val mainThreadExecutor = object : Executor {
    private val handler = Handler(Looper.getMainLooper())
    
    override fun execute(command: Runnable) {
      handler.post(command)
    }
  }
  
  override fun execute(block: () -> Unit) {
    mainThreadExecutor.execute(block)
  }
  
}