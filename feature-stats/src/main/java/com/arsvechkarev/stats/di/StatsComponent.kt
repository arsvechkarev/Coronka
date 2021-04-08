package com.arsvechkarev.stats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.stats.presentation.StatsViewModel
import core.di.CoreComponent.schedulers
import core.di.ModuleInterceptorManager.interceptModuleOrDefault

object StatsComponent {
  
  fun getViewModel(fragment: StatsFragment): StatsViewModel {
    return ViewModelProvider(fragment, statsViewModelFactory).get(StatsViewModel::class.java)
  }
  
  private val statsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      val statsModule = interceptModuleOrDefault<StatsModule> { DefaultStatsModule }
      @Suppress("UNCHECKED_CAST")
      return StatsViewModel(statsModule.statsUseCase, schedulers) as T
    }
  }
}