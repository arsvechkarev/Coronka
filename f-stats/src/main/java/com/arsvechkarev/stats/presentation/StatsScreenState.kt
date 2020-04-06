package com.arsvechkarev.stats.presentation

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
    val list: List<DisplayableItem>
  ) : StatsScreenState()
  
  class Failure(val reason: FailureReason) : StatsScreenState() {
    enum class FailureReason { NO_CONNECTION, TIMEOUT, UNKNOWN }
  }
}