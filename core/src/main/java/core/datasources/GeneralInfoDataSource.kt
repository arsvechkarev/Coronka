package core.datasources

import core.model.GeneralInfo
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