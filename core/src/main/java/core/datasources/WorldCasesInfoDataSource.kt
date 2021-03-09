package core.datasources

import core.WebApi
import core.model.DailyCase
import core.transformers.WorldCasesInfoTransformer
import io.reactivex.Observable

/**
 * Data source for retrieving world cases info
 */
interface WorldCasesInfoDataSource {
  
  /**
   * Returns list of daily cases wrapped as [Observable]
   */
  fun requestWorldDailyCases(): Observable<List<DailyCase>>
}

class WorldCasesInfoDataSourceImpl(private val webApi: WebApi) : WorldCasesInfoDataSource {
  
  override fun requestWorldDailyCases(): Observable<List<DailyCase>> {
    return webApi.request(URL).map(WorldCasesInfoTransformer::toDailyCases)
  }
  
  companion object {
    
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}