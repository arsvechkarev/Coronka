package com.arsvechkarev.database

import android.provider.BaseColumns._ID
import com.arsvechkarev.database.CountriesTable.COLUMN_CONFIRMED
import com.arsvechkarev.database.CountriesTable.COLUMN_COUNTRY_NAME
import com.arsvechkarev.database.CountriesTable.COLUMN_DEATHS
import com.arsvechkarev.database.CountriesTable.COLUMN_RECOVERED
import com.arsvechkarev.database.CountriesTable.TABLE_NAME


const val SQL_CREATE_COUNTRIES_TABLE = """
CREATE TABLE $TABLE_NAME
|($_ID INTEGER PRIMARY KEY,
|$COLUMN_COUNTRY_NAME TEXT,
|$COLUMN_CONFIRMED TEXT,
|$COLUMN_DEATHS TEXT,
|$COLUMN_RECOVERED TEXT)"""
