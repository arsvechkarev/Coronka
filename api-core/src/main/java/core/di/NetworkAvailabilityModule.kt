package core.di

import core.NetworkAvailabilityNotifier
import core.rx.RxReceivingChannel
import core.rx.RxSendingChannel

interface NetworkAvailabilityModule {
  
  val networkAvailabilitySendingChannel: RxSendingChannel<Unit>
  
  val networkAvailabilityChannel: RxReceivingChannel<Unit>
  
  val networkAvailabilityNotifier: NetworkAvailabilityNotifier
}