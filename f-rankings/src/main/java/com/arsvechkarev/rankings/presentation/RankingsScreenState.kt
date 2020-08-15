package com.arsvechkarev.rankings.presentation

import core.recycler.SortableDisplayableItem
import core.state.BaseScreenState

sealed class RankingsScreenState : BaseScreenState() {
  
  class Success(val list: List<SortableDisplayableItem>) : RankingsScreenState()
}