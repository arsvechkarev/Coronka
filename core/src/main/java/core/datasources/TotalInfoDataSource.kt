package core.datasources

import core.model.TotalInfo
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