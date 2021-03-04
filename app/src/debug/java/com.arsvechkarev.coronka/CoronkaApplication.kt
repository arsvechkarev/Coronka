package com.arsvechkarev.coronka

import com.arsvechkarev.common.CommonModulesSingletons
import timber.log.Timber

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    CommonModulesSingletons.initDefault(applicationContext)
  }
}