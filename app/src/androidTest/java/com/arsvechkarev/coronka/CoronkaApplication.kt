package com.arsvechkarev.coronka

import com.arsvechkarev.common.CoreDiComponent
import timber.log.Timber

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    CoreDiComponent.initCustomNetworker(applicationContext, FakeNetworker)
  }
}