package core.datasourcesimpl

import core.WebApi
import core.datasources.TotalInfoDataSource
import core.jsontransformers.AllCountriesTransformer
import core.model.TotalInfo
import io.reactivex.Observable

class TotalInfoDataSourceImpl(private val webApi: WebApi) : TotalInfoDataSource {
  
  override fun requestTotalInfo(): Observable<TotalInfo> {
    return webApi.request(URL).map(AllCountriesTransformer::toTotalData)
  }
  
  companion object {
    
    const val URL = "https://api.covid19api.com/summary"
  }
}