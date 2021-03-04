package com.arsvechkarev.common

import core.Networker
import core.model.GeneralInfo
import core.toGeneralInfo
import io.reactivex.Observable

class GeneralInfoRepository(private val networker: Networker) {
  
  fun getGeneralInfo(): Observable<GeneralInfo> {
    return networker.request(URL).map(String::toGeneralInfo)
  }
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}