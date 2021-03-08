package com.arsvechkarev.common

import core.WebApi
import core.jsontransformers.GeneralInfoTransformer
import core.model.GeneralInfo
import io.reactivex.Observable

class GeneralInfoDataSource(private val webApi: WebApi) {
  
  fun getGeneralInfo(): Observable<GeneralInfo> {
    return webApi.request(URL).map(GeneralInfoTransformer::toGeneralInfo)
  }
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}