package com.arsvechkarev.stats.presentation

import core.model.TotalData
import core.model.WorldCasesInfo
import core.state.BaseScreenState

sealed class StatsScreenState : BaseScreenState() {
  
  class LoadedFromCache(
    val data: TotalData
  ) : StatsScreenState()
  
  class LoadedWorldCasesInfo(
    val worldCasesInfo: WorldCasesInfo
  ) : StatsScreenState()
}