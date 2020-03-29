package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.di.SingletonsInjector
import com.arsvechkarev.map.presentation.CountriesInfoViewModel
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import core.ApplicationConfig.Threader
import core.NetworkConnection

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): CountriesInfoViewModel {
    val connection = SingletonsInjector.connection
    val countriesInfoExecutor = SingletonsInjector.countriesInfoExecutor
    val sqLiteExecutor = SingletonsInjector.countriesSQLiteExecutor
    val interactor = CountriesInfoInteractor(Threader, countriesInfoExecutor, sqLiteExecutor)
    return ViewModelProviders.of(fragment, mapViewModelFactory(connection, Threader, interactor))
        .get(CountriesInfoViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    connection: NetworkConnection,
    threader: Threader,
    interactor: CountriesInfoInteractor
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = CountriesInfoViewModel(connection, threader, interactor)
      return viewModel as T
    }
  }
}