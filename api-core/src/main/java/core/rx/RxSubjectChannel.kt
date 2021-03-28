package core.rx

import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

/**
 * Implementation of [RxSendingChannel] and [RxReceivingChannel] that uses [Subject] for channel
 * communication
 *
 * @see RxSendingChannel
 * @see RxReceivingChannel
 */
class RxSubjectChannel<T>(
  private val subject: Subject<T>
) : RxSendingChannel<T>, RxReceivingChannel<T> {
  
  @Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
  override fun send(value: T) {
    subject.onNext(value)
  }
  
  override fun receive(onReceive: (T) -> Unit): Disposable {
    return subject.subscribe(onReceive)
  }
}