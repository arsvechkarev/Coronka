package com.arsvechkarev.stats.domain

import core.model.data.GeneralInfo
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Data source for retrieving [GeneralInfo]
 */
fun interface GeneralInfoDataSource {
  
  @GET("/all")
  fun requestGeneralInfo(): Single<GeneralInfo>
  
  companion object {
    
    /** Base url for retrofit */
    const val BASE_URL = "https://coronavirus-19-api.herokuapp.com/"
  }
}