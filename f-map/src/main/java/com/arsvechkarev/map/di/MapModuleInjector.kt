package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.CountriesInfoViewModel
import com.arsvechkarev.map.repository.CountriesFirebaseExecutor
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import com.arsvechkarev.map.repository.CountriesSQLiteExecutor
import core.ApplicationConfig.backgroundWorker
import core.ApplicationConfig.ioWorker
import core.ApplicationConfig.mainThreadWorker

object MapModuleInjector {
  
  
  fun provideViewModel(fragment: MapFragment): CountriesInfoViewModel {
    val repository = CountriesInfoInteractor(backgroundWorker, CountriesFirebaseExecutor(mainThreadWorker, ioWorker),
      CountriesSQLiteExecutor(mainThreadWorker, ioWorker))
    return ViewModelProviders.of(fragment, mapViewModelFactory(repository))
        .get(CountriesInfoViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(interactor: CountriesInfoInteractor): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = CountriesInfoViewModel(interactor)
        return viewModel as T
      }
    }
  }
}