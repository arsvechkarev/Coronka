package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.connection
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.NetworkConnection
import core.concurrency.AndroidThreader

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val factory = mapViewModelFactory(connection, allCountriesRepository)
    return ViewModelProviders.of(fragment, factory).get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    connection: NetworkConnection,
    repository: AllCountriesRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MapViewModel(AndroidThreader, connection, repository)
      return viewModel as T
    }
  }
}