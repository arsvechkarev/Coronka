package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.di.CoreComponent.gsonConverterFactory
import core.di.CoreComponent.networkAvailabilityNotifier
import core.di.CoreComponent.okHttpClient
import core.di.CoreComponent.rxJava2CallAdapterFactory
import core.di.CoreComponent.schedulers
import core.di.CoreComponent.webApi
import core.di.ModuleInterceptorManager.interceptModuleOrDefault

object StatsComponent {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    return ViewModelProvider(fragment, statsViewModelFactory).get(StatsViewModel::class.java)
  }
  
  private val statsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      val statsModule = interceptModuleOrDefault<StatsModule> {
        DefaultStatsModule(rxJava2CallAdapterFactory, gsonConverterFactory, okHttpClient, webApi)
      }
      @Suppress("UNCHECKED_CAST")
      return StatsViewModel(statsModule.generalInfoDataSource, statsModule.worldCasesInfoDataSource,
        networkAvailabilityNotifier, schedulers) as T
    }
  }
}