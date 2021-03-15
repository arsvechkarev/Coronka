package com.arsvechkarev.rankings.presentation

import core.BaseScreenState
import core.model.Country
import core.recycler.DifferentiableItem

class FilteredCountries(val list: List<DifferentiableItem>) : BaseScreenState()

class LoadedCountries(val list: List<DifferentiableItem>) : BaseScreenState()

class ShowCountryInfo(
  val country: Country,
  val deathRate: Float,
  val percentInCountry: Float
) : BaseScreenState()