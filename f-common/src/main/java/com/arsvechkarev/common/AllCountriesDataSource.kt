package com.arsvechkarev.common

import core.WebApi
import core.jsontransformers.AllCountriesTransformer
import core.model.TotalData
import io.reactivex.Observable

class AllCountriesDataSource(private val webApi: WebApi) {
  
  fun getTotalData(): Observable<TotalData> {
    return webApi.request(URL).map(AllCountriesTransformer::toTotalData)
  }
  
  companion object {
    
    const val URL = "https://api.covid19api.com/summary"
  }
}