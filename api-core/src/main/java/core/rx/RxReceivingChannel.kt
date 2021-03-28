package core.rx

import io.reactivex.disposables.Disposable

/**
 * Observes values received from channel
 *
 * @see RxSendingChannel
 * @see RxSubjectChannel
 */
interface RxReceivingChannel<T> {
  
  /**
   * Calls [onReceive] when channel notifies about next value
   */
  fun receive(onReceive: (T) -> Unit): Disposable
}