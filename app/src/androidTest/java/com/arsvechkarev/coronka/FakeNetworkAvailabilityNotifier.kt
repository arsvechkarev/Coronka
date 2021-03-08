package com.arsvechkarev.coronka

import android.net.ConnectivityManager.OnNetworkActiveListener
import core.NetworkAvailabilityNotifier

object FakeNetworkAvailabilityNotifier : NetworkAvailabilityNotifier {
  
  private val listeners = ArrayList<OnNetworkActiveListener>()
  
  fun notifyNetworkAvailable() {
    listeners.forEach(OnNetworkActiveListener::onNetworkActive)
  }
  
  override fun registerListener(listener: OnNetworkActiveListener) {
    listeners.add(listener)
  }
  
  override fun unregisterListener(listener: OnNetworkActiveListener) {
    listeners.remove(listener)
  }
}