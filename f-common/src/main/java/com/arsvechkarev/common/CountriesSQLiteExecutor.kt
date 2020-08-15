package com.arsvechkarev.common

import android.content.ContentValues
import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import core.Loggable
import core.dao.CountriesDao
import core.dao.CountriesTable
import core.log
import core.model.Country

class CountriesSQLiteExecutor(
  private val countriesDao: CountriesDao
) : Loggable {
  
  override val logTag = "Request_CountriesSQLiteExecutor"
  
  fun isTableNotEmpty(): Boolean {
    DatabaseManager.instance.readableDatabase.use {
      return DatabaseExecutor.isTableNotEmpty(it, CountriesTable.TABLE_NAME)
    }
  }
  
  fun getCountries(): List<Country> {
    val cursor = DatabaseExecutor.readAll(DatabaseManager.instance.readableDatabase,
      CountriesTable.TABLE_NAME)
    return countriesDao.getCountriesList(cursor)
  }
  
  fun saveCountriesInfo(list: List<Country>) {
    log { "loading countries to cache" }
    DatabaseManager.instance.writableDatabase.use { database ->
      val contentValues = ContentValues()
      for (country in list) {
        countriesDao.populateWithValues(country, contentValues)
        DatabaseExecutor.insertOrUpdate(
          database, CountriesTable.TABLE_NAME, CountriesTable.COLUMN_COUNTRY_ID,
          country.id, contentValues
        )
        contentValues.clear()
      }
    }
  }
}