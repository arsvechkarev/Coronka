package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.arsvechkarev.database.DatabaseManager
import core.FontManager
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    DatabaseManager.init(applicationContext)
    CountriesRequestManager.init(applicationContext)
    FontManager.init(applicationContext)
  }
  
}