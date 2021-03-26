package com.arsvechkarev.coronka

import com.arsvechkarev.coronka.fakes.FakeCoreModule
import com.arsvechkarev.featurescommon.di.CommonFeaturesComponent
import com.arsvechkarev.featurescommon.di.CommonFeaturesModuleImpl
import core.di.CoreComponent
import timber.log.Timber

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }
  
  override fun initializeDiComponents() {
    val fakeCoreModule = FakeCoreModule()
    val commonFeaturesModule = CommonFeaturesModuleImpl(fakeCoreModule)
    CoreComponent.initialize(fakeCoreModule)
    CommonFeaturesComponent.initialize(commonFeaturesModule)
  }
}