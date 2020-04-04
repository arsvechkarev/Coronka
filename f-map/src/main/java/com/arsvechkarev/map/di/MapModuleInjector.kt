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
import core.Application.Threader
import core.NetworkConnection

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val repository = CommonRepository(generalInfoListenableExecutor,
      countriesInfoListenableExecutor)
    return ViewModelProviders.of(fragment, mapViewModelFactory(Threader,
      SingletonsInjector.connection, repository))
        .get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    threader: Threader,
    connection: NetworkConnection,
    repository: CommonRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MapViewModel(threader, connection, repository)
      return viewModel as T
    }
  }
}