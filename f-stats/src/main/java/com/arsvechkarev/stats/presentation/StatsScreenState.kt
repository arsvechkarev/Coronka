package com.arsvechkarev.stats.presentation

import core.model.TotalData
import core.state.BaseScreenState

sealed class StatsScreenState : BaseScreenState() {
  
  class LoadedFromCache(
    val data: TotalData
  ) : StatsScreenState()
  
  class LoadedFromNetwork(
    val data: TotalData
  ) : StatsScreenState()
}