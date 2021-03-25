package com.arsvechkarev.common.di

object CommonFeaturesComponent {
  
  private lateinit var commonFeaturesModule: CommonFeaturesModule
  
  val countriesDataSource get() = commonFeaturesModule.countriesDataSource
  
  val countriesMetaInfoRepository get() = commonFeaturesModule.countriesMetaInfoRepository
  
  fun initialize(commonFeaturesModule: CommonFeaturesModule) {
    this.commonFeaturesModule = commonFeaturesModule
  }
}