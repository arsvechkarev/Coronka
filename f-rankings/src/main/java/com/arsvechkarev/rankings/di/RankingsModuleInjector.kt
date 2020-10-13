package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.common.AllCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesRepository
import com.arsvechkarev.common.CommonModulesSingletons.metaInfoRepository
import com.arsvechkarev.common.CountriesMetaInfoRepository
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel

object RankingsModuleInjector {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    val factory = rankingsViewModelFactory(allCountriesRepository, metaInfoRepository)
    return ViewModelProviders.of(fragment, factory).get(RankingsViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun rankingsViewModelFactory(
    allCountriesRepository: AllCountriesRepository,
    metaInfoRepository: CountriesMetaInfoRepository
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = RankingsViewModel(allCountriesRepository, metaInfoRepository)
      return viewModel as T
    }
  }
}