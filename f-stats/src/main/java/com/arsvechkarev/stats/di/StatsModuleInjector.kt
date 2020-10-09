package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.CommonModulesSingletons.networker
import com.arsvechkarev.common.GeneralInfoRepository
import com.arsvechkarev.common.WorldCasesInfoRepository
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.storage.Saver

object StatsModuleInjector {
  
  fun provideViewModel(fragment: StatsFragment): StatsViewModel {
    val saver = Saver(GeneralInfoRepository.SAVER_FILENAME, fragment.requireContext())
    val generalInfoRepository = GeneralInfoRepository(networker, saver)
    val factory = statsViewModelFactory(generalInfoRepository, WorldCasesInfoRepository(networker))
    return ViewModelProviders.of(fragment, factory).get(StatsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun statsViewModelFactory(
    generalInfoRepository: GeneralInfoRepository,
    worldCasesInfoRepository: WorldCasesInfoRepository,
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = StatsViewModel(generalInfoRepository, worldCasesInfoRepository)
      return viewModel as T
    }
  }
}