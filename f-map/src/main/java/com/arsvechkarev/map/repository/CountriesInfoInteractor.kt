package com.arsvechkarev.map.repository

import core.log.Loggable
import core.model.Country

class CountriesInfoInteractor(
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val generalInfoExecutor: GeneralInfoExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor
) : Loggable {
  
  override val tag = "CountriesInfoInteractor"
  
  /**
   * Downloading info by country and uploading it to the database
   */
  fun updateCountriesInfo(
    allowUseCache: Boolean = false,
    onSuccess: (List<Country>) -> Unit,
    onFailure: (Throwable) -> Unit = {}
  ) {
    if (allowUseCache && sqLiteExecutor.isTableNotEmpty()) {
      sqLiteExecutor.readFromDatabase(onSuccess)
      return
    }
    countriesInfoExecutor.getCountriesInfoAsync(onSuccess = {
      sqLiteExecutor.saveCountriesInfo(it)
      onSuccess(it)
    }, onFailure = onFailure)
  }
}