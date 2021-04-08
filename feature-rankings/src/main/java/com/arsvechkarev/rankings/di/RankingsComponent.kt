package com.arsvechkarev.rankings.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.rankings.presentation.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsFragment
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import core.di.CoreComponent.schedulers
import core.di.ModuleInterceptorManager.interceptModuleOrDefault
import core.model.ui.DisplayableCountry

object RankingsComponent {
  
  fun provideViewModel(fragment: RankingsFragment): RankingsViewModel {
    return ViewModelProvider(fragment, rankingsViewModelFactory).get(RankingsViewModel::class.java)
  }
  
  fun provideAdapter(onClick: (DisplayableCountry) -> Unit): RankingsAdapter {
    return RankingsAdapter(onClick)
  }
  
  @Suppress("UNCHECKED_CAST")
  private val rankingsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      val rankingsModule = interceptModuleOrDefault<RankingsModule> { DefaultRankingsModule }
      return RankingsViewModel(rankingsModule.rankingsInteractor, schedulers) as T
    }
  }
}