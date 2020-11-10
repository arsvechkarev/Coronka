package com.arsvechkarev.map.di

import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.metaInfoRepository
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    return fragment.createViewModel(allCountriesRepository, metaInfoRepository, AndroidSchedulers)
  }
}