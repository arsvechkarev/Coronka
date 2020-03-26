package com.arsvechkarev.map.repository

import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.google.firebase.database.DatabaseError
import core.ResultHandler
import core.log.Loggable
import core.log.debug
import core.model.Country
import core.model.GeneralInfo

class CountriesInfoFacade(
  private val firebaseExecutor: CountriesFirebaseExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor,
  private val countriesRequestManager: CountriesRequestManager,
  private val generalInfoRepository: GeneralInfoRepository
) : Loggable {
  
  override val tag = "CountriesInfoInteractor"
  
  fun loadGeneralInfo(
    onSuccess: (GeneralInfo) -> Unit,
    onFailure: (Throwable) -> Unit = {}
  ) {
    generalInfoRepository.getGeneralInfo(allowCache = false,
      resultHandler = object : ResultHandler<GeneralInfo, Throwable>() {
        
        override fun onSuccess(value: GeneralInfo) {
          onSuccess(value)
        }
        
        override fun onFailure(error: Throwable) {
          onFailure(error)
        }
      })
  }
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(
    onSuccess: (List<Country>) -> Unit,
    onFailure: (DatabaseError) -> Unit = {}
  ) {
    val isRequestAllowed = countriesRequestManager.isRequestAllowed()
    debug { "is request allowed = $isRequestAllowed" }
    if (isRequestAllowed) {
      countriesRequestManager.disallowRequest()
      firebaseExecutor.getDataAsync(onSuccess = {
        onSuccess(it)
        sqLiteExecutor.saveCountriesInfo(it)
      }, onFailure = {
        onFailure(it)
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