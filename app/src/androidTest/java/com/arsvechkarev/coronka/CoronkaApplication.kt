package com.arsvechkarev.coronka

import com.arsvechkarev.common.CommonModulesSingletons

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    CommonModulesSingletons.initCustomNetworker(applicationContext, FakeNetworker)
  }
}