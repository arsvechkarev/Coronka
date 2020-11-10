package com.arsvechkarev.rankings.di

import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.metaInfoRepository
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel
import core.model.DisplayableCountry

object RankingsModuleInjector {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    return fragment.createViewModel(allCountriesRepository, metaInfoRepository, AndroidSchedulers)
  }
  
  fun provideAdapter(onClick: (DisplayableCountry) -> Unit): RankingsAdapter {
    return RankingsAdapter(onClick)
  }
}