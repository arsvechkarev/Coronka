package com.arsvechkarev.map.presentation

import core.BaseScreenState
import core.model.domain.Country
import core.model.ui.CountryOnMapMetaInfo

class LoadedCountries(
  val iso2ToCountryMapMetaInfo: Map<String, CountryOnMapMetaInfo>,
) : BaseScreenState()

class FoundCountry(
  val iso2ToCountryMapMetaInfo: Map<String, CountryOnMapMetaInfo>,
  val country: Country
) : BaseScreenState()