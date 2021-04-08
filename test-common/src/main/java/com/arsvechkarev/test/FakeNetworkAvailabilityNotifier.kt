package com.arsvechkarev.test

import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.di.NetworkAvailabilityModule
import core.rx.RxSubjectChannel
import io.reactivex.subjects.PublishSubject

class FakeNetworkAvailabilityNotifier : NetworkAvailabilityNotifier {
  
  val channel = RxSubjectChannel<Unit>(PublishSubject.create())
  
  fun notifyNetworkAvailable() {
    channel.send(Unit)
  }
  
  override fun registerListener(listener: NetworkListener) = Unit
  
  override fun unregisterListener(listener: NetworkListener) = Unit
}

class FakeNetworkAvailabilityModule(
  notifier: FakeNetworkAvailabilityNotifier
) : NetworkAvailabilityModule {
  
  override val networkAvailabilitySendingChannel = notifier.channel
  override val networkAvailabilityChannel = notifier.channel
  override val networkAvailabilityNotifier = notifier
}