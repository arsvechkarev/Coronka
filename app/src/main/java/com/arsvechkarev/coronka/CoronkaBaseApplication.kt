package com.arsvechkarev.coronka

import android.app.Application
import androidx.annotation.CallSuper
import base.resources.Fonts
import com.arsvechkarev.featurescommon.di.CommonFeaturesComponent
import com.arsvechkarev.featurescommon.di.CommonFeaturesModuleImpl
import com.arsvechkarev.viewdsl.ContextHolder
import com.jakewharton.threetenabp.AndroidThreeTen
import core.di.CoreComponent
import coreimpl.DefaultCoreModule

open class CoronkaBaseApplication : Application() {
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    ContextHolder.init(applicationContext)
    Fonts.init(applicationContext)
    AndroidThreeTen.init(applicationContext)
    initializeDiComponents()
  }
  
  open fun initializeDiComponents() {
    val coreModule = DefaultCoreModule(applicationContext)
    val commonFeaturesModule = CommonFeaturesModuleImpl(coreModule)
    CoreComponent.initialize(coreModule)
    CommonFeaturesComponent.initialize(commonFeaturesModule)
  }
}