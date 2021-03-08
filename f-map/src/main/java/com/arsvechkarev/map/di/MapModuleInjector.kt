package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.AndroidSchedulers
import core.CoreDiComponent.countriesMetaInfoDataSource
import core.CoreDiComponent.networkAvailabilityNotifier
import core.CoreDiComponent.totalInfoDataSource

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    return ViewModelProviders.of(fragment, mapViewModelFactory).get(MapViewModel::class.java)
  }
  
  private val mapViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return MapViewModel(totalInfoDataSource, countriesMetaInfoDataSource,
            networkAvailabilityNotifier, AndroidSchedulers) as T
        }
      }
    }
}