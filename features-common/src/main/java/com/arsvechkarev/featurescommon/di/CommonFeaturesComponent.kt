package com.arsvechkarev.featurescommon.di

object CommonFeaturesComponent {
  
  private lateinit var commonFeaturesModule: CommonFeaturesModule
  
  val countriesInformationDatabase get() = commonFeaturesModule.countriesInformationDatabase
  
  val countriesDataSource get() = commonFeaturesModule.countriesDataSource
  
  fun initialize(commonFeaturesModule: CommonFeaturesModule) {
    this.commonFeaturesModule = commonFeaturesModule
  }
}