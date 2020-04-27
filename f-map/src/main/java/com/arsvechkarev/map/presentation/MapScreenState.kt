package com.arsvechkarev.map.presentation

import core.model.Country
import core.state.BaseScreenState

sealed class MapScreenState : BaseScreenState() {
  
  object Loading : MapScreenState()
  
  class LoadedFromCache(
    val countries: List<Country>
  ) : MapScreenState()
  
  class LoadedFromNetwork(
    val countries: List<Country>
  ) : MapScreenState()
  
  class FoundCountry(
    val countries: List<Country>,
    val country: Country
  ) : MapScreenState()
}