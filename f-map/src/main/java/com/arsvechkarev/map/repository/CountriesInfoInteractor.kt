package com.arsvechkarev.map.repository

import com.google.firebase.database.DatabaseError
import core.async.Worker
import core.model.CountryInfo

class CountriesInfoInteractor(
  private val backgroundWorker: Worker,
  private val firebaseExecutor: CountriesFirebaseExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor
) {
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(
    onSuccess: (List<CountryInfo>) -> Unit,
    onError: (DatabaseError) -> Unit = {}
  ) {
    backgroundWorker.submit {
      if (sqLiteExecutor.isTableNotEmpty()) {
        sqLiteExecutor.readFromDatabase(onSuccess)
      } else {
        firebaseExecutor.getDataAsync(onSuccess = {
          onSuccess(it)
          sqLiteExecutor.saveCountriesInfo(it)
        }, onError = {
          onError(it)
        })
      }
    }
  }
}