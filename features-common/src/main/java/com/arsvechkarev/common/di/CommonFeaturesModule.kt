package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.CountriesMetaInfoRepository
import com.arsvechkarev.common.domain.TotalInfoDataSource

interface CommonFeaturesModule {
  
  val totalInfoDataSource: TotalInfoDataSource
  
  val countriesMetaInfoRepository: CountriesMetaInfoRepository
}