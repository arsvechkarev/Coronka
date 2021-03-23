package com.arsvechkarev.coronka

import com.arsvechkarev.common.di.CommonFeaturesComponent
import com.arsvechkarev.common.di.CommonFeaturesModuleImpl
import com.arsvechkarev.coronka.fakes.FakeCoreModule
import core.di.CoreComponent
import coreimpl.DateTimeFormatterModuleImpl
import timber.log.Timber

class CoronkaApplication : CoronkaBaseApplication() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }
  
  override fun initializeDiComponents() {
    val fakeCoreModule = FakeCoreModule()
    val dateTimeFormatterModule = DateTimeFormatterModuleImpl(applicationContext)
    val commonFeaturesModule = CommonFeaturesModuleImpl(fakeCoreModule)
    CoreComponent.initialize(fakeCoreModule, dateTimeFormatterModule)
    CommonFeaturesComponent.initialize(commonFeaturesModule)
  }
}