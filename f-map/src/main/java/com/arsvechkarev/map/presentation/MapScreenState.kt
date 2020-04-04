package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.GeneralInfo

sealed class MapScreenState {
  
  object Loading : MapScreenState()
  
  object LoadingCountryInfo : MapScreenState()
  
  class LoadedFromCache(
    val countriesList: List<Country>,
    val generalInfo: GeneralInfo,
    val lastUpdateTime: String
  ) : MapScreenState()
  
  class LoadedFromNetwork(
    val countriesList: List<Country>,
    val generalInfo: GeneralInfo
  ) : MapScreenState()
  
  class FoundCountry(val country: Country) : MapScreenState()
  
  class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}