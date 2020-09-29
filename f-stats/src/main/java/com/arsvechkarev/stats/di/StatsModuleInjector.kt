package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.connection
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.NetworkConnection

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val factory = statsViewModelFactory(connection, allCountriesRepository)
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    allCountriesRepository: AllCountriesRepository,
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(connection, allCountriesRepository)
      return viewModel as T
    }
  }
}