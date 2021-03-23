package core.di

import core.DateTimeFormatter

interface DateTimeFormatterModule {
  
  val timeFormatter: DateTimeFormatter
}