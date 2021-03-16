package com.arsvechkarev.map.presentation

import core.BaseScreenState
import core.model.Country
import core.model.CountryOnMap

class LoadedCountries(
  val iso2ToCountryMap: Map<String, CountryOnMap>,
) : BaseScreenState

class FoundCountry(
  val iso2ToCountryMap: Map<String, CountryOnMap>,
  val country: Country
) : BaseScreenState