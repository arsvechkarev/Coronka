package com.arsvechkarev.common.domain

import core.model.GeneralInfo
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Data source for retrieving [GeneralInfo]
 */
interface GeneralInfoDataSource {
  
  /**
   * Returns list of [GeneralInfo] wrapped as [Single]
   */
  @GET("/all")
  fun requestGeneralInfo(): Single<GeneralInfo>
  
  companion object {
    
    /** Base url for retrofit */
    const val BASE_URL = "https://coronavirus-19-api.herokuapp.com/"
  }
}