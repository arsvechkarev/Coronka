package com.arsvechkarev.map.presentation

import core.model.CountryInfo

sealed class CountriesInfoState {
  
  class Success(val countriesInfo: List<CountryInfo>) : CountriesInfoState()
  
  class Failure(val reason: FailureReason) : CountriesInfoState() {
    
    enum class FailureReason { NO_CONNECTION, UNKNOWN }
  }
  
}