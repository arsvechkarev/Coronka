package com.arsvechkarev.map.presentation

import core.model.Country
import core.model.CountryOnMap
import core.state.BaseScreenState

class LoadedCountries(
  val iso2ToCountryMap: Map<String, CountryOnMap>,
) : BaseScreenState()

class FoundCountry(
  val iso2ToCountryMap: Map<String, CountryOnMap>,
  val country: Country
) : BaseScreenState()