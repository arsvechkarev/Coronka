package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.di.SingletonsInjector.connection
import com.arsvechkarev.common.di.SingletonsInjector.countriesInfoExecutor
import com.arsvechkarev.common.di.SingletonsInjector.countriesSQLiteExecutor
import com.arsvechkarev.common.di.SingletonsInjector.networker
import com.arsvechkarev.common.repositories.CountriesInfoInteractor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.storage.Saver
import core.Application.Singletons.applicationContext
import core.Application.Threader
import core.NetworkConnection

object StatsModuleInjector {
  
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val countriesSaver = Saver(CountriesInfoInteractor.SAVER_FILENAME, applicationContext)
    val interactor = CountriesInfoInteractor(Threader, countriesInfoExecutor,
      countriesSQLiteExecutor, countriesSaver)
    val generalSaver = Saver(GeneralInfoExecutor.SAVER_FILENAME, applicationContext)
    val generalInfoExecutor = GeneralInfoExecutor(Threader, networker, generalSaver)
    val factory = mapViewModelFactory(connection, Threader, interactor, generalInfoExecutor)
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun mapViewModelFactory(
    connection: NetworkConnection,
    threader: Threader,
    interactor: CountriesInfoInteractor,
    generalInfoExecutor: GeneralInfoExecutor
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(connection, threader, interactor, generalInfoExecutor)
      return viewModel as T
    }
  }
  
}