package com.arsvechkarev.map.presentation

import core.model.Country

sealed class MapScreenState {
  
  class CountriesLoaded(val countriesList: List<Country>) : MapScreenState()
  class ShowingCountryInfo(val country: Country, val countriesList: List<Country>) : MapScreenState()
  object Failure: MapScreenState()
}