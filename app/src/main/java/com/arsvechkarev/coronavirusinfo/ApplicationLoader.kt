package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseHelper
import com.jakewharton.threetenabp.AndroidThreeTen
import core.viewbuilding.Dimens
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes
import timber.log.Timber
import viewdsl.ContextHolder
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
    ContextHolder.init(applicationContext)
    CountriesMetaInfoDatabaseHelper.init(applicationContext)
    CommonModulesSingletons.init(applicationContext)
    Fonts.init(applicationContext)
    TextSizes.init(applicationContext)
    Dimens.init(applicationContext)
    AndroidThreeTen.init(this)
  }
}