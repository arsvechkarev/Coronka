package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.CommonRepository
import com.arsvechkarev.common.di.SingletonsInjector
import com.arsvechkarev.common.di.SingletonsInjector.countriesInfoListenableExecutor
import com.arsvechkarev.common.di.SingletonsInjector.generalInfoListenableExecutor
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.NetworkConnection
import core.concurrency.AndroidThreader

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val repository = CommonRepository(generalInfoListenableExecutor,
      countriesInfoListenableExecutor)
    val factory = mapViewModelFactory(SingletonsInjector.connection, repository)
    return ViewModelProviders.of(fragment, factory).get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    connection: NetworkConnection,
    repository: CommonRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MapViewModel(AndroidThreader, connection, repository)
      return viewModel as T
    }
  }
}