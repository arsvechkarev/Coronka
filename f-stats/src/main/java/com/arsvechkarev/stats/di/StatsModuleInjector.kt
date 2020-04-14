package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.common.di.SingletonsInjector.connection
import com.arsvechkarev.common.di.SingletonsInjector.countriesInfoListenableExecutor
import com.arsvechkarev.common.di.SingletonsInjector.generalInfoListenableExecutor
import com.arsvechkarev.stats.domain.ListFilterer
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.storage.dao.PopulationsDao
import core.Application.Threader
import core.NetworkConnection

object StatsModuleInjector {
  
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val repository = CommonRepository(generalInfoListenableExecutor,
      countriesInfoListenableExecutor)
    val filterer = ListFilterer(Threader, PopulationsDao())
    val factory = statsViewModelFactory(connection, Threader, repository, filterer)
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    threader: Threader,
    repository: CommonRepository,
    filterer: ListFilterer
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(connection, threader, repository, filterer)
      return viewModel as T
    }
  }
  
}