package com.arsvechkarev.rankings.presentation

import core.model.OptionType
import core.model.WorldRegion
import core.recycler.SortableDisplayableItem
import core.state.BaseScreenState

sealed class RankingsScreenState : BaseScreenState() {
  
  class Filtered(
    val list: List<SortableDisplayableItem>
  ) : RankingsScreenState()
  
  class Loaded(
    val list: List<SortableDisplayableItem>,
    val optionType: OptionType,
    val worldRegion: WorldRegion
  ) : RankingsScreenState()
}