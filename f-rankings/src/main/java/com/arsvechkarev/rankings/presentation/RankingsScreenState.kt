package com.arsvechkarev.rankings.presentation

import core.model.DisplayableCountry
import core.state.BaseScreenState

sealed class RankingsScreenState : BaseScreenState() {
  
  class Loaded(val list: List<DisplayableCountry>) : RankingsScreenState()
}