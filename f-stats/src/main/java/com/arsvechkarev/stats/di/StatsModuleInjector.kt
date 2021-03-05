package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.common.CoreDiComponent.networker
import com.arsvechkarev.common.GeneralInfoDataSource
import com.arsvechkarev.common.WorldCasesInfoRepository
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.concurrency.AndroidSchedulers

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    return ViewModelProvider(fragment, statsViewModelFactory).get(StatsViewModel::class.java)
  }
  
  private val statsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          val generalRepository = GeneralInfoDataSource(networker)
          val worldCasesRepository = WorldCasesInfoRepository(networker)
          return StatsViewModel(generalRepository, worldCasesRepository, AndroidSchedulers) as T
        }
      }
    }
}