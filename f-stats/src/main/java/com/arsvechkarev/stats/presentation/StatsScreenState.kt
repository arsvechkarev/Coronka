package com.arsvechkarev.stats.presentation

import core.model.Country
import core.model.DisplayableCountry
import core.model.GeneralInfo
import core.recycler.DisplayableItem

sealed class StatsScreenState {
  
  object LoadingGeneralInfo : StatsScreenState()
  
  object LoadingCountriesInfo : StatsScreenState()
  
  class GeneralInfoLoaded(val generalInfo: GeneralInfo) : StatsScreenState()
  
  class CountriesLoaded(val countries: List<Country>) : StatsScreenState()
  
  class LoadedAll(
    val items: List<DisplayableItem>,
    val isFromCache: Boolean,
    val lastUpdateTime: String
  ) : StatsScreenState()
  
  class FilteredCountries(
    val countries: List<DisplayableCountry>
  ) : StatsScreenState()
  
  class Failure(val reason: FailureReason) : StatsScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}