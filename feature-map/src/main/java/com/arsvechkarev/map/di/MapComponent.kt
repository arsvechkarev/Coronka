package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesMetaInfoRepository
import com.arsvechkarev.common.di.CommonFeaturesComponent.totalInfoDataSource
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import core.di.CoreComponent.networkAvailabilityNotifier
import core.di.CoreComponent.schedulers

object MapComponent {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    return ViewModelProviders.of(fragment, mapViewModelFactory).get(MapViewModel::class.java)
  }
  
  private val mapViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return MapViewModel(totalInfoDataSource, countriesMetaInfoRepository,
        networkAvailabilityNotifier, schedulers) as T
    }
  }
}