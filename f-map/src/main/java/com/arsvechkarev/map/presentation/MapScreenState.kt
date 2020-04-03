package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.GeneralInfo

sealed class MapScreenState {
  
  object LoadingCountries : MapScreenState()
  
  object LoadingCountryInfo : MapScreenState()
  
  object LoadingGeneralInfo : MapScreenState()
  
  class LoadedAll(
    val countriesList: List<Country>,
    val generalInfo: GeneralInfo,
    val isFromCache: Boolean,
    val lastUpdateTime: String
  ) : MapScreenState()
  
  class FoundCountry(val country: Country) : MapScreenState()
  
  class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}