package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.concurrency.AndroidThreader

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val factory = mapViewModelFactory(allCountriesRepository)
    return ViewModelProviders.of(fragment, factory).get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(repository: AllCountriesRepository) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MapViewModel(repository, AndroidThreader)
      return viewModel as T
    }
  }
}