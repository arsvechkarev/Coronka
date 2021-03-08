package com.arsvechkarev.coronka

import core.CoreDiComponent
import timber.log.Timber

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    CoreDiComponent.initCustom(FakeWebApi, FakeNetworkAvailabilityNotifier, applicationContext)
  }
}