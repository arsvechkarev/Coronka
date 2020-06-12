package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.storage.DatabaseManager
import core.Application
import core.Colors
import core.FontManager
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    Application.Singletons.init(applicationContext)
    DatabaseManager.init(applicationContext)
    FontManager.init(applicationContext)
    CommonModulesSingletons.init(applicationContext)
    Colors.init(applicationContext)
  }
}