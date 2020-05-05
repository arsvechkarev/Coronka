package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.common.di.SingletonsInjector
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
    SingletonsInjector.init(applicationContext)
    Colors.setup(applicationContext)
  }
}