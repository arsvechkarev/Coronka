package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.arsvechkarev.database.DatabaseHolder
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    DatabaseHolder.init(applicationContext)
    CountriesRequestManager.init(applicationContext)
  }
  
}