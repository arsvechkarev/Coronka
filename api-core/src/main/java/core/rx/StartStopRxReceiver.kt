package core.rx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

/**
 * Lifecycle observer that observes [RxReceivingChannel] and automatically subscribes/unsubscribes
 * in [onStart] and [onStop]
 */
class StartStopRxReceiver<T>(
  private val receivingChannel: RxReceivingChannel<T>,
  private val onReceive: (T) -> Unit
) : LifecycleObserver {
  
  private var disposable: Disposable? = null
  
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    disposable = receivingChannel.receive(onReceive)
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun onStop() {
    disposable?.dispose()
  }
}