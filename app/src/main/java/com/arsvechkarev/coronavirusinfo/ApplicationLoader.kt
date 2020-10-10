package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.storage.countries.CountriesMetaInfoHelper
import core.Application
import core.Colors
import core.FontManager
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    Application.init(applicationContext)
    CountriesMetaInfoHelper.init(applicationContext)
    FontManager.init(applicationContext)
    CommonModulesSingletons.init(applicationContext)
    Colors.init(applicationContext)
  }
}