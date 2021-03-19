package core.datasources

import core.WebApi
import core.model.TotalInfo
import core.transformers.AllCountriesTransformer
import io.reactivex.Single

/**
 * Data source for retrieving [TotalInfo]
 */
interface TotalInfoDataSource {
  
  /**
   * Returns **[TotalInfo]** wrapped as [Single]
   */
  fun requestTotalInfo(): Single<TotalInfo>
  
  companion object {
    
    const val URL = "https://api.covid19api.com/summary"
  }
}

class TotalInfoDataSourceImpl(private val webApi: WebApi) : TotalInfoDataSource {
  
  override fun requestTotalInfo(): Single<TotalInfo> {
    return webApi.request(TotalInfoDataSource.URL).map(AllCountriesTransformer::toTotalData)
  }
}