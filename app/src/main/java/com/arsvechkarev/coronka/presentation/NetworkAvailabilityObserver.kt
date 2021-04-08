package com.arsvechkarev.coronka.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import core.NetworkAvailabilityNotifier
import core.NetworkListener
import core.di.CoreComponent.networkAvailabilitySendingChannel

class NetworkAvailabilityObserver(
  private val notifier: NetworkAvailabilityNotifier,
) : NetworkListener, LifecycleObserver {
  
  private val mainHandler = Handler(Looper.getMainLooper())
  
  override fun onNetworkAvailable() {
    mainHandler.post { networkAvailabilitySendingChannel.send(Unit) }
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onStart() {
    notifier.registerListener(this)
  }
  
  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  fun onStop() {
    notifier.unregisterListener(this)
  }
}