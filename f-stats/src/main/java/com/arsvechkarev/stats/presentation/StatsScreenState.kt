package com.arsvechkarev.stats.presentation

import com.arsvechkarev.stats.list.OptionType
import core.model.DisplayableCountry
import core.recycler.DisplayableItem

sealed class StatsScreenState {
  
  object Loading : StatsScreenState()
  
  class LoadedFromCache(
    val items: List<DisplayableItem>,
    val lastUpdateTime: String
  ) : StatsScreenState()
  
  class LoadedFromNetwork(
    val items: List<DisplayableItem>
  ) : StatsScreenState()
  
  class FilteredCountries(
    val optionType: OptionType,
    val countries: List<DisplayableCountry>
  ) : StatsScreenState()
  
  class Failure(val reason: FailureReason) : StatsScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}