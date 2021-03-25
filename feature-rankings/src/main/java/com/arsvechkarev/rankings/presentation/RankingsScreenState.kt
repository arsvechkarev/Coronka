package com.arsvechkarev.rankings.presentation

import api.recycler.DifferentiableItem
import core.BaseScreenState
import core.model.ui.CountryFullInfo

class FilteredCountries(val list: List<DifferentiableItem>) : BaseScreenState

class LoadedCountries(val list: List<DifferentiableItem>) : BaseScreenState

class ShowCountryInfo(val countryFullInfo: CountryFullInfo) : BaseScreenState