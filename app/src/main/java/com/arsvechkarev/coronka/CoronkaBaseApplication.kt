package com.arsvechkarev.coronka

import android.app.Application
import androidx.annotation.CallSuper
import base.resources.Fonts
import com.arsvechkarev.common.di.CommonFeaturesComponent
import com.arsvechkarev.common.di.CommonFeaturesModuleImpl
import com.arsvechkarev.viewdsl.ContextHolder
import com.jakewharton.threetenabp.AndroidThreeTen
import core.di.CoreComponent
import coreimpl.CoreModuleImpl

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
    val coreModule = CoreModuleImpl(applicationContext)
    val commonFeaturesModule = CommonFeaturesModuleImpl(coreModule)
    CoreComponent.initialize(coreModule)
    CommonFeaturesComponent.initialize(commonFeaturesModule)
  }
}