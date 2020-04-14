package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.GeneralInfo
import core.state.BaseScreenState

sealed class MapScreenState : BaseScreenState() {
  
  object Loading : MapScreenState()
  
  class LoadedFromCache(
    val countries: List<Country>,
    val generalInfo: GeneralInfo,
    val lastUpdateTime: String
  ) : MapScreenState()
  
  class LoadedFromNetwork(
    val countries: List<Country>,
    val generalInfo: GeneralInfo
  ) : MapScreenState()
  
  class FoundCountry(
    val countries: List<Country>,
    val generalInfo: GeneralInfo,
    val country: Country
  ) : MapScreenState()
  
  class Failure(val reason: FailureReason) : MapScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}