package com.arsvechkarev.common.di

import com.arsvechkarev.common.domain.TotalInfoDataSource
import com.arsvechkarev.common.repository.CountriesMetaInfoRepository

interface CommonFeaturesModule {
  
  val totalInfoDataSource: TotalInfoDataSource
  
  val countriesMetaInfoRepository: CountriesMetaInfoRepository
}