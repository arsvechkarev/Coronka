package com.arsvechkarev.stats.presentation

import com.arsvechkarev.stats.list.InfoType
import core.model.DisplayableCountry
import core.model.GeneralInfo

sealed class StatsScreenState {
  
  object LoadingGeneralInfo : StatsScreenState()
  
  object LoadingCountriesInfo : StatsScreenState()
  
  class GeneralInfoLoaded(val generalInfo: GeneralInfo) : StatsScreenState()
  
  class CountriesLoaded(val countries: List<DisplayableCountry>) : StatsScreenState()
  
  class LoadedAll(
    val infoType: InfoType,
    val generalInfo: GeneralInfo,
    val displayableCountries: List<DisplayableCountry>
  ) : StatsScreenState()
  
  class Failure(val reason: FailureReason) : StatsScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}