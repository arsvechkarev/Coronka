package com.arsvechkarev.stats.presentation

import com.arsvechkarev.stats.list.OptionType
import core.model.DisplayableCountry
import core.recycler.DisplayableItem

sealed class StatsScreenState {
  
  object Loading : StatsScreenState()
  
  class LoadedAll(
    val items: List<DisplayableItem>,
    val isFromCache: Boolean,
    val lastUpdateTime: String
  ) : StatsScreenState()
  
  class FilteredCountries(
    val optionType: OptionType,
    val countries: List<DisplayableCountry>
  ) : StatsScreenState()
  
  class Failure(val reason: FailureReason) : StatsScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}