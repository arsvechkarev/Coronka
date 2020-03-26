package com.arsvechkarev.map.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import core.ApplicationConfig
import core.model.Country

class CountriesFirebaseExecutor(
  threader: ApplicationConfig.Threader
) {
  
  private val mainThreadWorker = threader.mainThreadWorker
  private val backgroundWorker = threader.backgroundWorker
  
  private val countriesInfoName = "countries_info"
  
  fun getDataAsync(onSuccess: (List<Country>) -> Unit, onFailure: (DatabaseError) -> Unit = {}) {
    FirebaseDatabase.getInstance().getReference(countriesInfoName)
        .addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onCancelled(error: DatabaseError) {
            onFailure(error)
          }
          
          override fun onDataChange(snapshot: DataSnapshot) {
            backgroundWorker.submit {
              val infoData = ArrayList<Country>()
              snapshot.children.forEach {
                val valueArr = it.value!!.toString().split("|")
                infoData.add(
                  Country(
                    countryId = valueArr[0].toInt(),
                    countryName = it.key!!.toString(),
                    countryCode = valueArr[1],
                    confirmed = valueArr[2],
                    deaths = valueArr[3],
                    recovered = valueArr[4],
                    latitude = valueArr[5],
                    longitude = valueArr[6])
                )
              }
              mainThreadWorker.submit { onSuccess(infoData) }
            }
          }
        })
  }
}