package com.arsvechkarev.stats.presentation

import com.arsvechkarev.common.repositories.CountriesInfoExecutor
import com.arsvechkarev.common.repositories.GeneralInfoExecutor
import core.ApplicationConfig
import core.NetworkConnection
import core.NetworkViewModel

class StatsViewModel(
  connection: NetworkConnection,
  private val threader: ApplicationConfig.Threader,
  private val countriesInfoExecutor: CountriesInfoExecutor,
  private val generalInfoExecutor: GeneralInfoExecutor
) : NetworkViewModel(connection) {
  
  
  fun loadData() {
  
  }
  
}