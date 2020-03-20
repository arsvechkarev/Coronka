package com.arsvechkarev.database

import com.arsvechkarev.database.CountriesTable.COLUMN_CONFIRMED
import com.arsvechkarev.database.CountriesTable.COLUMN_COUNTRY_ID
import com.arsvechkarev.database.CountriesTable.COLUMN_COUNTRY_NAME
import com.arsvechkarev.database.CountriesTable.COLUMN_DEATHS
import com.arsvechkarev.database.CountriesTable.COLUMN_LATITUDE
import com.arsvechkarev.database.CountriesTable.COLUMN_LONGITUDE
import com.arsvechkarev.database.CountriesTable.COLUMN_RECOVERED
import com.arsvechkarev.database.CountriesTable.TABLE_NAME


object Queries {
  
  val SQL_CREATE_COUNTRIES_TABLE = """
CREATE TABLE $TABLE_NAME
|($COLUMN_COUNTRY_ID INTEGER PRIMARY KEY,
|$COLUMN_COUNTRY_NAME TEXT,
|$COLUMN_CONFIRMED TEXT,
|$COLUMN_DEATHS TEXT,
|$COLUMN_RECOVERED TEXT,
|$COLUMN_LATITUDE TEXT,
|$COLUMN_LONGITUDE TEXT)""".trimMargin()
  
  fun selectAll(tableName: String) = "SELECT * FROM $tableName"
}
