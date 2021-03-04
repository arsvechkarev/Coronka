package com.arsvechkarev.coronka

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.coronka.fakeapi.FakeNetworker

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    CommonModulesSingletons.initCustomNetworker(applicationContext, FakeNetworker)
  }
}