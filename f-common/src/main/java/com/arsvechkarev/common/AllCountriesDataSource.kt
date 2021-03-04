package com.arsvechkarev.common

import core.Networker
import core.jsontransformers.AllCountriesTransformer
import core.model.TotalData
import io.reactivex.Observable

class AllCountriesDataSource(private val networker: Networker) {
  
  fun getTotalData(): Observable<TotalData> {
    return networker.request(URL).map(AllCountriesTransformer::toTotalData)
  }
  
  companion object {
    
    const val URL = "https://api.covid19api.com/summary"
  }
}