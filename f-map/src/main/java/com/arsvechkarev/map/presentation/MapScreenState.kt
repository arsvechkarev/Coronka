package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.Location
import core.state.BaseScreenState

sealed class MapScreenState : BaseScreenState() {
  
  class Loaded(
    val countries: List<Country>,
    val iso2ToLocations: Map<String, Location>
  ) : MapScreenState()
  
  class FoundCountry(
    val countries: List<Country>,
    val iso2ToLocations: Map<String, Location>,
    val country: Country
  ) : MapScreenState()
}