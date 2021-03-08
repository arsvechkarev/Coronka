package core.datasources

import core.model.NewsItemWithPicture
import io.reactivex.Observable

/**
 * Data source for retrieving list of [NewsItemWithPicture]
 */
interface NewYorkTimesNewsDataSource {
  
  /**
   * Returns list of [NewsItemWithPicture] wrapped as [Observable]
   */
  fun requestLatestNews(page: Int): Observable<List<NewsItemWithPicture>>
}