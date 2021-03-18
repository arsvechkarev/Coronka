package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.AndroidSchedulers
import core.CoreDiComponent.networkAvailabilityNotifier
import core.CoreDiComponent.webApiFactory
import core.datasources.GeneralInfoDataSourceImpl
import core.datasources.WorldCasesInfoDataSourceImpl

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    return ViewModelProvider(fragment, statsViewModelFactory).get(StatsViewModel::class.java)
  }
  
  private val statsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          val webApi = webApiFactory.create()
          val generalInfoDataSourceImpl = GeneralInfoDataSourceImpl(webApi)
          val worldCasesInfoDataSourceImpl = WorldCasesInfoDataSourceImpl(webApi)
          return StatsViewModel(generalInfoDataSourceImpl, worldCasesInfoDataSourceImpl,
            networkAvailabilityNotifier, AndroidSchedulers) as T
        }
      }
    }
}