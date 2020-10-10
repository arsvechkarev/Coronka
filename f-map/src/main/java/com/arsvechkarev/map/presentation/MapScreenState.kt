package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.CountryOnMap
import core.state.BaseScreenState

sealed class MapScreenState : BaseScreenState() {
  
  class Loaded(
    val iso2ToCountryMap: Map<String, CountryOnMap>,
  ) : MapScreenState()
  
  class FoundCountry(
    val iso2ToCountryMap: Map<String, CountryOnMap>,
    val country: Country
  ) : MapScreenState()
}