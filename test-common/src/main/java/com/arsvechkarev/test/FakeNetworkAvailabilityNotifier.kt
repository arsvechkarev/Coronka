package com.arsvechkarev.test

import core.NetworkAvailabilityNotifier
import core.NetworkListener

class FakeNetworkAvailabilityNotifier : NetworkAvailabilityNotifier {
  
  private val listeners = ArrayList<NetworkListener>()
  
  fun notifyNetworkAvailable() {
    listeners.forEach(NetworkListener::onNetworkAvailable)
  }
  
  override fun registerListener(listener: NetworkListener) {
    listeners.add(listener)
  }
  
  override fun unregisterListener(listener: NetworkListener) {
    listeners.remove(listener)
  }
}