package com.arsvechkarev.coronka

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseHelper
import com.arsvechkarev.viewdsl.ContextHolder
import com.jakewharton.threetenabp.AndroidThreeTen
import core.viewbuilding.Fonts
import timber.log.Timber
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    ContextHolder.init(applicationContext)
    CountriesMetaInfoDatabaseHelper.init(applicationContext)
    CommonModulesSingletons.init(applicationContext)
    Fonts.init(applicationContext)
    AndroidThreeTen.init(applicationContext)
  }
}