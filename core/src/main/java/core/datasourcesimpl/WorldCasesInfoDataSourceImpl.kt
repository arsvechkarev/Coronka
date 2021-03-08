package core.datasourcesimpl

import core.WebApi
import core.datasources.WorldCasesInfoDataSource
import core.jsontransformers.WorldCasesInfoTransformer
import core.model.DailyCase
import io.reactivex.Observable

class WorldCasesInfoDataSourceImpl(private val webApi: WebApi) : WorldCasesInfoDataSource {
  
  override fun requestWorldDailyCases(): Observable<List<DailyCase>> {
    return webApi.request(URL).map(WorldCasesInfoTransformer::toDailyCases)
  }
  
  companion object {
    
    const val URL = "https://raw.githubusercontent.com/arsvechkarev/coronavirus-data/main/daily_cases.json"
  }
}