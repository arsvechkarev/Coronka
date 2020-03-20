package com.arsvechkarev.map.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import core.ApplicationConfig
import core.async.BackgroundWorker
import core.async.MainThreadWorker
import core.async.Worker
import core.model.CountryInfo

class CountriesFirebaseExecutor(
  private val mainThreadWorker: Worker,
  private val backgroundWorker: Worker
) {
  
  private val countriesInfoName = "countries_info"
  
  fun getDataAsync(onSuccess: (List<CountryInfo>) -> Unit, onError: (DatabaseError) -> Unit = {}) {
    FirebaseDatabase.getInstance().getReference(countriesInfoName)
        .addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) { onError(error) }
          
          override fun onDataChange(snapshot: DataSnapshot) {
            backgroundWorker.submit {
              val infoData = ArrayList<CountryInfo>()
              var id = 1
              snapshot.children.forEach {
                val valueArr = it.value!!.toString().split("|")
                infoData.add(
                  CountryInfo(
                    countryId = id,
                    countryName = it.key!!.toString(),
                    confirmed = valueArr[0],
                    deaths = valueArr[1],
                    recovered = valueArr[2],
                    latitude = valueArr[3],
                    longitude = valueArr[4])
                )
                id++
              }
              mainThreadWorker.submit { onSuccess(infoData) }
            }
          }
        })
  }
}