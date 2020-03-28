package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.map.presentation.CountriesInfoViewModel
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.repository.CountriesInfoExecutor
import com.arsvechkarev.map.repository.CountriesInfoInteractor
import com.arsvechkarev.map.repository.CountriesSQLiteExecutor
import com.arsvechkarev.map.repository.GeneralInfoExecutor
import com.arsvechkarev.network.NetworkConnectionImpl
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig.Threader
import core.NetworkConnection

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): CountriesInfoViewModel {
    val networker = Networker()
    val connection: NetworkConnection = NetworkConnectionImpl(fragment.requireContext())
    val countriesInfoExecutor = CountriesInfoExecutor(Threader, networker)
    val sqLiteExecutor = CountriesSQLiteExecutor(Threader)
    val saver = Saver(GeneralInfoExecutor.SAVER_FILENAME, fragment.requireContext())
    val generalInfoRepository = GeneralInfoExecutor(Threader, networker, saver)
    val interactor = CountriesInfoInteractor(
      countriesInfoExecutor, generalInfoRepository, sqLiteExecutor
    )
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