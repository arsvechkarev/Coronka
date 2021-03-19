package core.datasources

import core.WebApi
import core.model.DailyCase
import core.transformers.WorldCasesInfoTransformer
import io.reactivex.Single

/**
 * Data source for retrieving world cases info
 */
interface WorldCasesInfoDataSource {
  
  /**
   * Returns list of daily cases wrapped as [Single]
   */
  fun requestWorldDailyCases(): Single<List<DailyCase>>
  
  companion object {
    
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}

class WorldCasesInfoDataSourceImpl(private val webApi: WebApi) : WorldCasesInfoDataSource {
  
  override fun requestWorldDailyCases(): Single<List<DailyCase>> {
    return webApi.request(WorldCasesInfoDataSource.URL)
        .map(WorldCasesInfoTransformer::toDailyCases)
  }
}