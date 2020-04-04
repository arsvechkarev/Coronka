package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.Repository
import com.arsvechkarev.common.di.SingletonsInjector.connection
import com.arsvechkarev.common.di.SingletonsInjector.countriesInfoListenableExecutor
import com.arsvechkarev.common.di.SingletonsInjector.generalInfoListenableExecutor
import com.arsvechkarev.common.di.SingletonsInjector.repositorySaver
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.Application.Threader
import core.NetworkConnection

object StatsModuleInjector {
  
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val repository = Repository(repositorySaver, generalInfoListenableExecutor,
      countriesInfoListenableExecutor)
    val factory = statsViewModelFactory(connection, Threader, repository)
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    threader: Threader,
    repository: Repository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(connection, threader, repository)
      return viewModel as T
    }
  }
  
}