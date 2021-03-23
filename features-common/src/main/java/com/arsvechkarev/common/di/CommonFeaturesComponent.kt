package com.arsvechkarev.common.di

object CommonFeaturesComponent {
  
  private var commonFeaturesModule: CommonFeaturesModule? = null
  
  val totalInfoDataSource by lazy { commonFeaturesModule!!.totalInfoDataSource }
  
  val countriesMetaInfoRepository by lazy { commonFeaturesModule!!.countriesMetaInfoRepository }
  
  fun initialize(commonFeaturesModule: CommonFeaturesModule) {
    this.commonFeaturesModule = commonFeaturesModule
  }
}