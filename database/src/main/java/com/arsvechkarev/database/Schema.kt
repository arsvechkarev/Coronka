package com.arsvechkarev.database

import android.provider.BaseColumns

object CountriesTable : BaseColumns {
  const val TABLE_NAME = "info_main"
  
  const val COLUMN_COUNTRY_NAME = "name"
  const val COLUMN_CONFIRMED = "confirmed"
  const val COLUMN_DEATHS = "deaths"
  const val COLUMN_RECOVERED = "recovered"
}
