package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.connection
import com.arsvechkarev.common.CommonModulesSingletons.networker
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.stats.domain.ListFilterer
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.storage.Saver
import core.NetworkConnection
import core.dao.CountriesMetaInfoDao

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val generalInfoSaver = Saver(GeneralInfoRepository.SAVER_FILENAME, fragment.requireContext())
    val generalInfoRepository = GeneralInfoRepository(networker, generalInfoSaver)
    val filterer = ListFilterer(CountriesMetaInfoDao())
    val factory = statsViewModelFactory(
      connection, allCountriesRepository, generalInfoRepository, filterer
    )
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    connection: NetworkConnection,
    allCountriesRepository: AllCountriesRepository,
    generalInfoRepository: GeneralInfoRepository,
    filterer: ListFilterer
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(
        connection, allCountriesRepository, generalInfoRepository, filterer
      )
      return viewModel as T
    }
  }
}