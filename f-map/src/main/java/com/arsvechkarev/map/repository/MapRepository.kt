package com.arsvechkarev.map.repository

import android.content.ContentValues
import android.util.Log
import com.arsvechkarev.database.CountriesTable
import com.arsvechkarev.database.CountriesTable.COLUMN_COUNTRY_ID
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
    println("qwerty: loading started")
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
  
  private fun handleSuccess(
    snapshot: DataSnapshot,
    onLoaded: (List<CountryInfo>) -> Unit
  ) {
    println("qwerty: handle success")
    backgroundWorker.submit {
      println("qwerty: handle success bg")
      val list = getInfoList(snapshot)
      println("qwerty: got list")
      
      onLoaded(list)
      println("qwerty: on loaded completed")
      list.forEach {
        println("qwerty: $it")
      }
      for (country in list) {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_COUNTRY_ID, country.countryId)
        contentValues.put(CountriesTable.COLUMN_COUNTRY_NAME, country.countryName)
        contentValues.put(CountriesTable.COLUMN_CONFIRMED, country.confirmed)
        contentValues.put(CountriesTable.COLUMN_DEATHS, country.deaths)
        contentValues.put(CountriesTable.COLUMN_RECOVERED, country.recovered)
        databaseExecutor.insertOrUpdate(
          CountriesTable.TABLE_NAME,
          COLUMN_COUNTRY_ID,
          country.countryId,
          contentValues
        )
      }
    }
  }
  
  private fun getInfoList(snapshot: DataSnapshot): List<CountryInfo> {
    val infoData = ArrayList<CountryInfo>()
    var id = 1
    snapshot.children.forEach {
      val valueArr = it.value!!.toString().split("|")
      infoData.add(CountryInfo(id, it.key!!.toString(), valueArr[0], valueArr[1], valueArr[2]))
      id++
    }
    return infoData
  }
}