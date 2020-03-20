package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.database.DatabaseManager
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    DatabaseManager.init(applicationContext)
  }
  
}