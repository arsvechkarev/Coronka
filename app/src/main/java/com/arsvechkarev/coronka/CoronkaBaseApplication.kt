package com.arsvechkarev.coronka

import android.app.Application
import androidx.annotation.CallSuper
import com.arsvechkarev.storage.countries.CountriesMetaInfoDatabaseHelper
import com.arsvechkarev.viewdsl.ContextHolder
import com.jakewharton.threetenabp.AndroidThreeTen
import core.viewbuilding.Fonts

open class CoronkaBaseApplication : Application() {
  
  @CallSuper
  override fun onCreate() {
    super.onCreate()
    ContextHolder.init(applicationContext)
    CountriesMetaInfoDatabaseHelper.init(applicationContext)
    Fonts.init(applicationContext)
    AndroidThreeTen.init(applicationContext)
  }
}