package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.NetworkConnection

object RankingsDiInjector {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    val factory = statsViewModelFactory(
      CommonModulesSingletons.connection,
      CommonModulesSingletons.allCountriesRepository
    )
    return ViewModelProviders.of(fragment, factory).get(RankingsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    allCountriesRepository: AllCountriesRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = RankingsViewModel(connection, allCountriesRepository)
      return viewModel as T
    }
  }
}