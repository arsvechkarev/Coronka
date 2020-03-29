package com.arsvechkarev.stats.presentation

import androidx.lifecycle.ViewModel
import com.arsvechkarev.common.repositories.CountriesInfoExecutor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import core.ApplicationConfig
import core.NetworkConnection

class StatsViewModel(
  private val connection: NetworkConnection,
  private val threader: ApplicationConfig.Threader,
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val generalInfoExecutor: GeneralInfoExecutor
) : ViewModel() {
  
  
  fun loadData() {
  
  }
  
}