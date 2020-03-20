package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.database.DatabaseExecutor
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import com.arsvechkarev.map.repository.MapRepository
import core.ApplicationConfig

object MapModuleInjector {
  
  private val backgroundWorker = ApplicationConfig.backgroundWorker
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val repository = MapRepository(backgroundWorker, DatabaseExecutor())
    return ViewModelProviders.of(fragment, mapViewModelFactory(repository))
      .get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(repository: MapRepository): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = MapViewModel(repository)
        return viewModel as T
      }
    }
  }
}