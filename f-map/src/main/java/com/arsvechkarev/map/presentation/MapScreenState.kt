package com.arsvechkarev.map.presentation

import core.model.Country

sealed class MapScreenState {
  
  object LoadingCountries : MapScreenState()
  
  object LoadingCountryInfo : MapScreenState()
  
  class CountriesLoaded(val countriesList: List<Country>, val isFromCache: Boolean, val lastUpdateTime: String) :
    MapScreenState()
  
  class FoundCountry(val country: Country) : MapScreenState()
  
  class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}