package com.arsvechkarev.map.repository

import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.google.firebase.database.DatabaseError
import core.log.Loggable
import core.log.debug
import core.model.Country

class CountriesInfoInteractor(
  private val firebaseExecutor: CountriesFirebaseExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val countriesRequestManager: CountriesRequestManager
) : Loggable {
  
  override val tag = "CountriesInfoInteractor"
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(
    onSuccess: (List<Country>) -> Unit,
    onError: (DatabaseError) -> Unit = {}
  ) {
    val isRequestAllowed = countriesRequestManager.isRequestAllowed()
    debug { "is request allowed = $isRequestAllowed" }
    if (isRequestAllowed) {
      countriesRequestManager.disallowRequest()
      firebaseExecutor.getDataAsync(onSuccess = {
        onSuccess(it)
        sqLiteExecutor.saveCountriesInfo(it)
      }, onError = {
        onError(it)
      })
    } else {
      if (sqLiteExecutor.isTableNotEmpty()) {
        sqLiteExecutor.readFromDatabase(onSuccess)
      } else {
        throw IllegalStateException("Request is not allowed, but table is empty")
      }
    }
  }
}