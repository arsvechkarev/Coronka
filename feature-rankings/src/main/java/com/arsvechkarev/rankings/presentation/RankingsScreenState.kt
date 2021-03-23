package com.arsvechkarev.rankings.presentation

import api.recycler.DifferentiableItem
import core.BaseScreenState
import core.model.Country

class FilteredCountries(val list: List<DifferentiableItem>) : BaseScreenState

class LoadedCountries(val list: List<DifferentiableItem>) : BaseScreenState

class ShowCountryInfo(
  val country: Country,
  val deathRate: Float,
  val percentInCountry: Float
) : BaseScreenState