package com.arsvechkarev.rankings.di

import com.arsvechkarev.rankings.domain.CountriesFilterer
import com.arsvechkarev.rankings.domain.CountriesFiltererImpl
import core.di.Module

interface RankingsModule : Module {
  
  val countriesFilter: CountriesFilterer
}

object DefaultRankingsModule : RankingsModule {
  
  override val countriesFilter = CountriesFiltererImpl()
}