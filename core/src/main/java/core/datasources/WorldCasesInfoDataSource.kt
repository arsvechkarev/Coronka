package core.datasources

import core.model.DailyCase
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