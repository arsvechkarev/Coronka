package coreimpl

import android.content.Context
import core.di.DateTimeFormatterModule

class DateTimeFormatterModuleImpl(applicationContext: Context) : DateTimeFormatterModule {
  
  override val timeFormatter = EnglishDateTimeFormatter(applicationContext,
    ThreeTenAbpDateTimeCreator)
}