package com.arsvechkarev.map.presentation

import core.model.Country

sealed class MapScreenState {
  
  object StartLoadingCountries : MapScreenState()
  
  object StartLoadingCountryInfo : MapScreenState()
  
  class CountriesLoaded(val countriesList: List<Country>) : MapScreenState()
  
  class FoundCountry(val country: Country) : MapScreenState()
  
  class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}