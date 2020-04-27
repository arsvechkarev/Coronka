package com.arsvechkarev.stats.presentation

import core.recycler.DisplayableItem
import core.state.BaseScreenState

sealed class StatsScreenState : BaseScreenState() {
  
  object Loading : StatsScreenState()
  
  class LoadedFromCache(
    val items: List<DisplayableItem>
  ) : StatsScreenState()
  
  class LoadedFromNetwork(
    val items: List<DisplayableItem>
  ) : StatsScreenState()
  
  class FilteredCountries(
    val list: List<DisplayableItem>
  ) : StatsScreenState()
}