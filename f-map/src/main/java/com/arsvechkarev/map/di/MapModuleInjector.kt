package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.arsvechkarev.map.presentation.CountriesInfoViewModel
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.repository.CountriesFirebaseExecutor
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import com.arsvechkarev.map.repository.CountriesSQLiteExecutor
import core.ApplicationConfig.Threader

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): CountriesInfoViewModel {
    val firebaseExecutor = CountriesFirebaseExecutor(Threader)
    val sqLiteExecutor = CountriesSQLiteExecutor(Threader)
    val interactor = CountriesInfoInteractor(firebaseExecutor, sqLiteExecutor,
      CountriesRequestManager)
    return ViewModelProviders.of(fragment, mapViewModelFactory(Threader, interactor))
        .get(CountriesInfoViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    threader: Threader,
    interactor: CountriesInfoInteractor
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = CountriesInfoViewModel(threader, interactor)
      return viewModel as T
    }
  }
}