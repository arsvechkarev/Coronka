package core.datasources

import core.WebApi
import core.model.TotalInfo
import core.transformers.AllCountriesTransformer
import io.reactivex.Observable

/**
 * Data source for retrieving [TotalInfo]
 */
interface TotalInfoDataSource {
  
  /**
   * Returns **[TotalInfo]** wrapped as [Observable]
   */
  fun requestTotalInfo(): Observable<TotalInfo>
}

class TotalInfoDataSourceImpl(private val webApi: WebApi) : TotalInfoDataSource {
  
  override fun requestTotalInfo(): Observable<TotalInfo> {
    return webApi.request(URL).map(AllCountriesTransformer::toTotalData)
  }
  
  companion object {
    
    const val URL = "https://api.covid19api.com/summary"
  }
}