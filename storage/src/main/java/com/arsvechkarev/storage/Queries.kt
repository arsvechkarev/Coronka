package com.arsvechkarev.storage

import com.arsvechkarev.storage.CountriesTable.COLUMN_CONFIRMED
import com.arsvechkarev.storage.CountriesTable.COLUMN_COUNTRY_CODE
import com.arsvechkarev.storage.CountriesTable.COLUMN_COUNTRY_ID
import com.arsvechkarev.storage.CountriesTable.COLUMN_COUNTRY_NAME
import com.arsvechkarev.storage.CountriesTable.COLUMN_DEATHS
import com.arsvechkarev.storage.CountriesTable.COLUMN_LATITUDE
import com.arsvechkarev.storage.CountriesTable.COLUMN_LONGITUDE
import com.arsvechkarev.storage.CountriesTable.COLUMN_RECOVERED
import com.arsvechkarev.storage.CountriesTable.TABLE_NAME


object Queries {
  
  val SQL_CREATE_COUNTRIES_TABLE = """
CREATE TABLE $TABLE_NAME
|($COLUMN_COUNTRY_ID INTEGER PRIMARY KEY,
|$COLUMN_COUNTRY_NAME TEXT,
|$COLUMN_COUNTRY_CODE TEXT,
|$COLUMN_CONFIRMED TEXT,
|$COLUMN_DEATHS TEXT,
|$COLUMN_RECOVERED TEXT,
|$COLUMN_LATITUDE TEXT,
|$COLUMN_LONGITUDE TEXT)""".trimMargin()
  
  fun selectAll(tableName: String) = "SELECT * FROM $tableName"
}
