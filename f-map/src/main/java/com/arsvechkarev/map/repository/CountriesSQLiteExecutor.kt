package com.arsvechkarev.map.repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.arsvechkarev.database.CountriesTable
import com.arsvechkarev.database.DatabaseExecutor
import com.arsvechkarev.database.DatabaseHolder
import com.arsvechkarev.database.Queries
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker
import core.model.CountryInfo

class CountriesSQLiteExecutor(
  private val mainThreadWorker: Worker = MainThreadWorker(),
  private val backgroundWorker: Worker = BackgroundWorker.io()
) {
  
  fun isTableNotEmpty(): Boolean {
    DatabaseHolder.instance.readableDatabase.use {
      return DatabaseExecutor.isTableNotEmpty(it, CountriesTable.TABLE_NAME)
    }
  }
  
  fun readFromDatabase(onSuccess: (List<CountryInfo>) -> Unit) {
    executeWithReadableDatabase { database ->
      val query = Queries.selectAll(CountriesTable.TABLE_NAME)
      DatabaseExecutor.executeQuery(database, query, ::transformCursorToList) { list ->
        mainThreadWorker.submit { onSuccess(list) }
      }
    }
  }
  
  fun saveCountriesInfo(list: List<CountryInfo>) {
    executeWithWriteableDatabase {
      for (country in list) {
        val contentValues = ContentValues()
        contentValues.put(CountriesTable.COLUMN_COUNTRY_ID, country.countryId)
        contentValues.put(CountriesTable.COLUMN_COUNTRY_NAME, country.countryName)
        contentValues.put(CountriesTable.COLUMN_CONFIRMED, country.confirmed)
        contentValues.put(CountriesTable.COLUMN_DEATHS, country.deaths)
        contentValues.put(CountriesTable.COLUMN_RECOVERED, country.recovered)
        DatabaseExecutor.insertOrUpdate(
          it, CountriesTable.TABLE_NAME, CountriesTable.COLUMN_COUNTRY_ID,
          country.countryId, contentValues
        )
      }
    }
  }
  
  private fun transformCursorToList(cursor: Cursor): List<CountryInfo> {
    val infoData = ArrayList<CountryInfo>()
    while (cursor.moveToNext()) {
      val info = CountryInfo(
        cursor.getInt(cursor.getColumnIndex(CountriesTable.COLUMN_COUNTRY_ID)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_COUNTRY_NAME)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_CONFIRMED)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_DEATHS)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_RECOVERED)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_LATITUDE)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_LONGITUDE))
      )
      infoData.add(info)
    }
    cursor.close()
    return infoData
  }
  
  private fun executeWithReadableDatabase(block: (SQLiteDatabase) -> Unit) {
    backgroundWorker.submit { DatabaseHolder.instance.readableDatabase.use(block) }
  }
  
  private fun executeWithWriteableDatabase(block: (SQLiteDatabase) -> Unit) {
    backgroundWorker.submit { DatabaseHolder.instance.readableDatabase.use(block) }
  }
  
}