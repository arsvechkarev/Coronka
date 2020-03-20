package com.arsvechkarev.map.repository

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import com.arsvechkarev.database.CountriesTable
import com.arsvechkarev.database.DatabaseExecutor
import com.arsvechkarev.database.DatabaseManager
import com.arsvechkarev.database.Queries
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import core.async.Worker
import core.model.CountryInfo


class MapRepository(
  private val backgroundWorker: Worker,
  private val databaseExecutor: DatabaseExecutor
) {
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(onLoaded: (List<CountryInfo>) -> Unit) {
    println("qw: update")
    backgroundWorker.submit {
      println("qw: update")
      if (databaseExecutor.isTableNotEmpty(CountriesTable.TABLE_NAME)) {
        println("qw: read from db")
        performDatabaseQuery(onLoaded)
      } else {
        println("qw: do network call")
        FirebaseDatabase.getInstance().getReference("countries_info")
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
              println("qwerty: error")
              Log.e("qwerty", "error", error.toException())
            }
            
            override fun onDataChange(snapshot: DataSnapshot) {
              println("qwerty: success")
              handleSuccess(snapshot, onLoaded)
            }
          })
      }
    }
  }
  
  private fun performDatabaseQuery(onLoaded: (List<CountryInfo>) -> Unit) {
    DatabaseManager.instance.readableDatabase.use { database ->
      databaseExecutor.executeQuery(
        database, Queries.selectAll(CountriesTable.TABLE_NAME), ::transformCursorToList
      ) {
        onLoaded(it)
      }
    }
  }
  
  private fun saveToDatabase(list: List<CountryInfo>) {
    DatabaseManager.instance.writableDatabase.use {
      for (country in list) {
        val contentValues = ContentValues()
        contentValues.put(CountriesTable.COLUMN_COUNTRY_ID, country.countryId)
        contentValues.put(CountriesTable.COLUMN_COUNTRY_NAME, country.countryName)
        contentValues.put(CountriesTable.COLUMN_CONFIRMED, country.confirmed)
        contentValues.put(CountriesTable.COLUMN_DEATHS, country.deaths)
        contentValues.put(CountriesTable.COLUMN_RECOVERED, country.recovered)
        databaseExecutor.insertOrUpdate(
          it, CountriesTable.TABLE_NAME, CountriesTable.COLUMN_COUNTRY_ID,
          country.countryId, contentValues
        )
      }
    }
  }
  
  private fun handleSuccess(
    snapshot: DataSnapshot,
    onLoaded: (List<CountryInfo>) -> Unit
  ) {
    backgroundWorker.submit {
      val list = getInfoList(snapshot)
      onLoaded(list)
      saveToDatabase(list)
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
    return infoData
  }
  
  private fun getInfoList(snapshot: DataSnapshot): List<CountryInfo> {
    val infoData = ArrayList<CountryInfo>()
    var id = 1
    snapshot.children.forEach {
      val valueArr = it.value!!.toString().split("|")
      infoData.add(
        CountryInfo(
          id,
          it.key!!.toString(),
          valueArr[0],
          valueArr[1],
          valueArr[2],
          valueArr[3],
          valueArr[4]
        )
      )
      id++
    }
    return infoData
  }
}