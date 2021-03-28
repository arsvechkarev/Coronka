package core.rx

/**
 * Represent a channel for sending values
 *
 * @see RxReceivingChannel
 * @see RxSubjectChannel
 */
interface RxSendingChannel<T> {
  
  /** Sends [value] to all listeners */
  fun send(value: T)
}