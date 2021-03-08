package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.AndroidSchedulers
import core.CoreDiComponent.countriesMetaInfoDataSource
import core.CoreDiComponent.networkAvailabilityNotifier
import core.CoreDiComponent.totalInfoDataSource
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
          return RankingsViewModel(totalInfoDataSource, countriesMetaInfoDataSource,
            networkAvailabilityNotifier, AndroidSchedulers) as T
        }
      }
    }
}