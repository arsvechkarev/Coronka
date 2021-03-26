package com.arsvechkarev.featurescommon.di

import com.arsvechkarev.featurescommon.domain.CountriesDataSource
import core.Database

interface CommonFeaturesModule {
  
  val countriesInformationDatabase: Database
  
  val countriesDataSource: CountriesDataSource
}