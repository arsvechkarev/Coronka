package core.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Future

class MainThreadWorker : Worker {
  
  private val handler = Handler(Looper.getMainLooper())
  
  override fun submit(block: () -> Unit): Future<*>? {
    handler.post(block)
    return null
  }
}