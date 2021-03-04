package com.arsvechkarev.coronka

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.coronka.fakeapi.FakeAlwaysSuccessNetworker

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    CommonModulesSingletons.initCustomNetworker(applicationContext, FakeAlwaysSuccessNetworker)
  }
}