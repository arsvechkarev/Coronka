package com.arsvechkarev.map.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.countriesrequestmanager.CountriesRequestManager
import com.arsvechkarev.map.presentation.CountriesInfoViewModel
import com.arsvechkarev.map.presentation.MapFragment
import com.arsvechkarev.map.repository.CountriesFirebaseExecutor
import com.arsvechkarev.map.repository.CountriesInfoFacade
import com.arsvechkarev.map.repository.CountriesSQLiteExecutor
import com.arsvechkarev.map.repository.GeneralInfoRepository
import com.arsvechkarev.network.Networker
import com.arsvechkarev.storage.Saver
import core.ApplicationConfig.Threader

object MapModuleInjector {
  
  fun provideViewModel(fragment: MapFragment): CountriesInfoViewModel {
    val firebaseExecutor = CountriesFirebaseExecutor(Threader)
    val sqLiteExecutor = CountriesSQLiteExecutor(Threader)
    val saver = Saver(GeneralInfoRepository.SAVER_FILENAME, fragment.requireContext())
    val generalInfoRepository = GeneralInfoRepository(Threader, Networker(), saver)
    val interactor = CountriesInfoFacade(firebaseExecutor, sqLiteExecutor,
      CountriesRequestManager, generalInfoRepository)
    return ViewModelProviders.of(fragment, mapViewModelFactory(Threader, interactor))
        .get(CountriesInfoViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    threader: Threader,
    facade: CountriesInfoFacade
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = CountriesInfoViewModel(threader, facade)
      return viewModel as T
    }
  }
}