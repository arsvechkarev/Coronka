package com.arsvechkarev.rankings.presentation

import core.model.OptionType
import core.model.WorldRegion
import core.recycler.DifferentiableItem
import core.state.BaseScreenState

class FilteredCountries(
  val list: List<DifferentiableItem>
) : BaseScreenState()

class LoadedCountries(
  val list: List<DifferentiableItem>,
  val optionType: OptionType,
  val worldRegion: WorldRegion
) : BaseScreenState()
