package com.arsvechkarev.map.presentation

import core.model.Country
import core.state.BaseScreenState

sealed class MapScreenState : BaseScreenState() {
  
  class Loaded(
    val countries: List<Country>
  ) : MapScreenState()
  
  class FoundCountry(
    val countries: List<Country>,
    val country: Country
  ) : MapScreenState()
}