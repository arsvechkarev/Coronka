package core.datasources

import core.WebApi
import core.model.GeneralInfo
import core.transformers.GeneralInfoTransformer
import io.reactivex.Observable

/**
 * Data source for retrieving [GeneralInfo]
 */
interface GeneralInfoDataSource {
  
  /**
   * Returns list of [GeneralInfo] wrapped as [Observable]
   */
  fun requestGeneralInfo(): Observable<GeneralInfo>
}

class GeneralInfoDataSourceImpl(private val webApi: WebApi) : GeneralInfoDataSource {
  
  override fun requestGeneralInfo(): Observable<GeneralInfo> {
    return webApi.request(URL).map(GeneralInfoTransformer::toGeneralInfo)
  }
  
  companion object {
    
    const val URL = "https://coronavirus-19-api.herokuapp.com/all"
  }
}