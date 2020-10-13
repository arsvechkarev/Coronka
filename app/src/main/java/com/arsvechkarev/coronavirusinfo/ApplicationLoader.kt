package com.arsvechkarev.coronavirusinfo

import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseHelper
import core.Application
import core.viewbuilding.Dimens
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes
import android.app.Application as AndroidApp

class ApplicationLoader : AndroidApp() {
  
  override fun onCreate() {
    super.onCreate()
    Application.init(applicationContext)
    CountriesMetaInfoDatabaseHelper.init(applicationContext)
    CommonModulesSingletons.init(applicationContext)
    Fonts.init(applicationContext)
    TextSizes.init(applicationContext)
    Dimens.init(applicationContext)
  }
}