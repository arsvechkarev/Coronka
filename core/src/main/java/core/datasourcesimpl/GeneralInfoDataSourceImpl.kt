package core.datasourcesimpl

import core.WebApi
import core.datasources.GeneralInfoDataSource
import core.jsontransformers.GeneralInfoTransformer
import core.model.GeneralInfo
import io.reactivex.Observable

class GeneralInfoDataSourceImpl(private val webApi: WebApi) : GeneralInfoDataSource {
  
  override fun requestGeneralInfo(): Observable<GeneralInfo> {
    return webApi.request(URL).map(GeneralInfoTransformer::toGeneralInfo)
  }
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}