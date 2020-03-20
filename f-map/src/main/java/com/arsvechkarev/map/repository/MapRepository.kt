package com.arsvechkarev.map.repository

import android.content.ContentValues
import com.arsvechkarev.database.CountriesTable
import com.arsvechkarev.database.DatabaseExecutor
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
    FirebaseDatabase.getInstance().getReference("countries_info")
      .addListenerForSingleValueEvent(object : ValueEventListener {
        
        override fun onCancelled(error: DatabaseError) {
        }
        
        override fun onDataChange(snapshot: DataSnapshot) {
          handleSuccess(snapshot, onLoaded)
        }
      })
  }
  
  private fun handleSuccess(
    snapshot: DataSnapshot,
    onLoaded: (List<CountryInfo>) -> Unit
  ) {
    backgroundWorker.execute {
      val list = getInfoList(snapshot)
      onLoaded(list)
      val contentValues = ContentValues()
      for (country in list) {
        contentValues.put(CountriesTable.COLUMN_COUNTRY_NAME, country.countryName)
        contentValues.put(CountriesTable.COLUMN_CONFIRMED, country.confirmed)
        contentValues.put(CountriesTable.COLUMN_DEATHS, country.deaths)
        contentValues.put(CountriesTable.COLUMN_RECOVERED, country.recovered)
      }
      databaseExecutor.insert(CountriesTable.TABLE_NAME, contentValues)
    }
  }
  
  private fun getInfoList(snapshot: DataSnapshot): List<CountryInfo> {
    val infoData = ArrayList<CountryInfo>()
    snapshot.children.forEach {
      val valueArr = it.value!!.toString().split("|")
      infoData.add(CountryInfo(it.key!!.toString(), valueArr[0], valueArr[1], valueArr[2]))
    }
    return infoData
  }
}