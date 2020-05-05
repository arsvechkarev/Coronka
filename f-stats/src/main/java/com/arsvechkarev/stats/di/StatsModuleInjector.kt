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
import core.NetworkConnection
import core.concurrency.AndroidThreader

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val repository = CommonRepository(generalInfoListenableExecutor,
      countriesInfoListenableExecutor)
    val filterer = ListFilterer(AndroidThreader, PopulationsDao())
    val factory = statsViewModelFactory(connection, repository, filterer)
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    repository: CommonRepository,
    filterer: ListFilterer
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(AndroidThreader, connection, repository, filterer)
      return viewModel as T
    }
  }
}