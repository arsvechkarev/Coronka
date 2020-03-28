package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.storage.DatabaseManager
import core.ApplicationConfig.Threader
import core.FontManager
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    DatabaseManager.init(applicationContext)
    FontManager.init(applicationContext, Threader)
  }
}