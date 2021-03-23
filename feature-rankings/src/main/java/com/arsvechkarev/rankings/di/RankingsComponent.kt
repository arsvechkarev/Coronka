package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.common.di.CommonFeaturesComponent.countriesMetaInfoRepository
import com.arsvechkarev.common.di.CommonFeaturesComponent.totalInfoDataSource
import com.arsvechkarev.rankings.domain.CountriesFiltererImpl
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.di.CoreComponent.networkAvailabilityNotifier
import core.di.CoreComponent.schedulers
import core.di.CoreComponent.threader
import core.model.DisplayableCountry

object RankingsComponent {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    return ViewModelProvider(fragment, rankingsViewModelFactory).get(RankingsViewModel::class.java)
  }
  
  fun provideAdapter(onClick: (DisplayableCountry) -> Unit): RankingsAdapter {
    return RankingsAdapter(onClick, threader)
  }
  
  @Suppress("UNCHECKED_CAST")
  private val rankingsViewModelFactory: ViewModelProvider.Factory
    get() {
      return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return RankingsViewModel(
            totalInfoDataSource, countriesMetaInfoRepository, CountriesFiltererImpl(),
            networkAvailabilityNotifier, schedulers
          ) as T
        }
      }
    }
}