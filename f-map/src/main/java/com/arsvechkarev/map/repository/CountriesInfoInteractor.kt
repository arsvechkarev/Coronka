package com.arsvechkarev.map.repository

import com.arsvechkarev.common.repositories.CountriesInfoExecutor
import com.arsvechkarev.common.repositories.CountriesSQLiteExecutor
import core.Loggable
import core.model.Country

class CountriesInfoInteractor(
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val sqLiteExecutor: CountriesSQLiteExecutor
) : Loggable {
  
  override val logTag = "CountriesInfoInteractor"
  
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
  
  fun tryUpdateFromCache(onSuccess: (List<Country>) -> Unit) {
  
  }
}