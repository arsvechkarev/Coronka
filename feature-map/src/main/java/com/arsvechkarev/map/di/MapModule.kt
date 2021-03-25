package com.arsvechkarev.map.di

import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesDataSource
import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesInformationDatabase
import com.arsvechkarev.map.domain.DatabaseLocationsMapDataSource
import com.arsvechkarev.map.domain.MapInteractor
import core.di.CoreComponent.schedulers
import core.di.Module
import core.model.mappers.CountryEntitiesToCountriesMapper

interface MapModule : Module {
  
  val mapInteractor: MapInteractor
}

object DefaultMapModule : MapModule {
  
  override val mapInteractor: MapInteractor
    get() = MapInteractor(
      countriesDataSource, DatabaseLocationsMapDataSource(countriesInformationDatabase),
      CountryEntitiesToCountriesMapper(), schedulers
    )
}