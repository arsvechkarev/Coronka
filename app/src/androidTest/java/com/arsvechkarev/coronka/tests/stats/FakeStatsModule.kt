package com.arsvechkarev.coronka.tests.stats

import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.stats.di.StatsModule
import com.arsvechkarev.stats.domain.StatsUseCase
import coreimpl.AndroidSchedulers
import io.reactivex.Single

object FakeStatsModule : StatsModule {
  
  override val statsUseCase = StatsUseCase(
    generalInfoDataSource = { Single.just(DataProvider.getGeneralInfo()) },
    worldCasesInfoDataSource = { Single.just(DataProvider.getWorldCasesInfo()) },
    schedulers = AndroidSchedulers
  )
}