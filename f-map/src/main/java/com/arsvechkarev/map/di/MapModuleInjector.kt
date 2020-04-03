package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.di.SingletonsInjector
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.presentation.MapViewModel
import com.arsvechkarev.storage.Saver
import core.Application.Singletons.applicationContext
import core.Application.Threader
import core.NetworkConnection

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): MapViewModel {
    val connection = SingletonsInjector.connection
    val countriesInfoExecutor = SingletonsInjector.countriesInfoExecutor
    val generalInfoExecutor = SingletonsInjector.generalInfoExecutor
    val sqLiteExecutor = SingletonsInjector.countriesSQLiteExecutor
    val saver = Saver(CountriesInfoInteractor.SAVER_FILENAME, applicationContext)
    val interactor = CountriesInfoInteractor(Threader, countriesInfoExecutor, sqLiteExecutor, saver)
    return ViewModelProviders.of(fragment,
      mapViewModelFactory(connection, Threader, interactor, generalInfoExecutor))
        .get(MapViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    connection: NetworkConnection,
    threader: Threader,
    interactor: CountriesInfoInteractor,
    generalInfoExecutor: GeneralInfoExecutor
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = MapViewModel(connection, threader, interactor, generalInfoExecutor)
      return viewModel as T
    }
  }
}