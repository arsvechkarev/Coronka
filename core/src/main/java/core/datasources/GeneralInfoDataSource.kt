package core.datasources

import core.WebApi
import core.model.GeneralInfo
import core.transformers.GeneralInfoTransformer
import io.reactivex.Single

/**
 * Data source for retrieving [GeneralInfo]
 */
interface GeneralInfoDataSource {
  
  /**
   * Returns list of [GeneralInfo] wrapped as [Single]
   */
  fun requestGeneralInfo(): Single<GeneralInfo>
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}

class GeneralInfoDataSourceImpl(private val webApi: WebApi) : GeneralInfoDataSource {
  
  override fun requestGeneralInfo(): Single<GeneralInfo> {
    return webApi.request(GeneralInfoDataSource.URL).map(GeneralInfoTransformer::toGeneralInfo)
  }
}