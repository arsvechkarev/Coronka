package com.arsvechkarev.stats.di

import com.arsvechkarev.common.CommonModulesSingletons.networker
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.common.WorldCasesInfoRepository
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.concurrency.AndroidSchedulers
import core.extenstions.createViewModel

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val generalRepository = GeneralInfoRepository(networker)
    val worldCasesRepository = WorldCasesInfoRepository(networker)
    return fragment.createViewModel(generalRepository, worldCasesRepository, AndroidSchedulers)
  }
}