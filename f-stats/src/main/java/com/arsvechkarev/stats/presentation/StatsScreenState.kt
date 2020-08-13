package com.arsvechkarev.stats.presentation

import core.recycler.SortableDisplayableItem
import core.state.BaseScreenState

sealed class StatsScreenState : BaseScreenState() {
  
  object Loading : StatsScreenState()
  
  class LoadedFromCache(
    val items: List<SortableDisplayableItem>
  ) : StatsScreenState()
  
  class LoadedFromNetwork(
    val items: List<SortableDisplayableItem>
  ) : StatsScreenState()
  
  class FilteredCountries(
    val list: List<SortableDisplayableItem>
  ) : StatsScreenState()
}