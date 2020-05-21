package com.arsvechkarev.storage.dao

import android.content.ContentValues
import android.database.Cursor
import com.arsvechkarev.storage.CountriesTable
import core.model.Country

class CountriesDao {
  
  fun getCountriesList(cursor: Cursor): List<Country> {
    val infoData = ArrayList<Country>()
    while (cursor.moveToNext()) {
      val info = Country(
        cursor.getInt(cursor.getColumnIndex(CountriesTable.COLUMN_COUNTRY_ID)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_COUNTRY_NAME)),
        cursor.getString(cursor.getColumnIndex(CountriesTable.COLUMN_COUNTRY_CODE)),
        cursor.getInt(cursor.getColumnIndex(CountriesTable.COLUMN_CONFIRMED)),
        cursor.getInt(cursor.getColumnIndex(CountriesTable.COLUMN_DEATHS)),
        cursor.getInt(cursor.getColumnIndex(CountriesTable.COLUMN_RECOVERED))
      )
      infoData.add(info)
    }
    cursor.close()
    return infoData
  }
  
  fun populateWithValues(country: Country, contentValues: ContentValues) {
    contentValues.put(CountriesTable.COLUMN_COUNTRY_ID, country.id)
    contentValues.put(CountriesTable.COLUMN_COUNTRY_NAME, country.name)
    contentValues.put(CountriesTable.COLUMN_COUNTRY_CODE, country.iso2)
    contentValues.put(CountriesTable.COLUMN_CONFIRMED, country.confirmed)
    contentValues.put(CountriesTable.COLUMN_DEATHS, country.deaths)
    contentValues.put(CountriesTable.COLUMN_RECOVERED, country.recovered)
  }
}