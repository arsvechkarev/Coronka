package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.common.CommonModulesSingletons.allCountriesDataSource
import com.arsvechkarev.common.CommonModulesSingletons.metaInfoRepository
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.concurrency.AndroidSchedulers
import core.model.DisplayableCountry

object RankingsModuleInjector {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    return ViewModelProvider(fragment, rankingsViewModelFactory).get(RankingsViewModel::class.java)
  }
  
  fun provideAdapter(onClick: (DisplayableCountry) -> Unit): RankingsAdapter {
    return RankingsAdapter(onClick)
  }
  
  private val rankingsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return RankingsViewModel(allCountriesDataSource, metaInfoRepository,
            AndroidSchedulers) as T
        }
      }
    }
}