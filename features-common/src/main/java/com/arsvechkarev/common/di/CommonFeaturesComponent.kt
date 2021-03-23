package com.arsvechkarev.common.di

object CommonFeaturesComponent {
  
  private lateinit var commonFeaturesModule: CommonFeaturesModule
  
  val totalInfoDataSource get() = commonFeaturesModule.totalInfoDataSource
  
  val countriesMetaInfoRepository get() = commonFeaturesModule.countriesMetaInfoRepository
  
  fun initialize(commonFeaturesModule: CommonFeaturesModule) {
    this.commonFeaturesModule = commonFeaturesModule
  }
}