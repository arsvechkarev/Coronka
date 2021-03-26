package com.arsvechkarev.featurescommon.domain

import core.model.data.CountriesWrapper
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Data source for retrieving [CountriesWrapper]
 */
fun interface CountriesDataSource {
  
  @GET("/summary")
  fun requestCountries(): Single<CountriesWrapper>
  
  companion object {
    
    /** Base url for retrofit */
    const val BASE_URL = "https://api.covid19api.com/"
  }
}