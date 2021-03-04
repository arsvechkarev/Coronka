package com.arsvechkarev.common

import core.Networker
import core.jsontransformers.GeneralInfoTransformer
import core.model.GeneralInfo
import io.reactivex.Observable

class GeneralInfoDataSource(private val networker: Networker) {
  
  fun getGeneralInfo(): Observable<GeneralInfo> {
    return networker.request(URL).map(GeneralInfoTransformer::toGeneralInfo)
  }
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}