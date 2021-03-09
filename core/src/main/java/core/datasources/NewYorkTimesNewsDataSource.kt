package core.datasources

import core.TimeFormatter
import core.WebApi
import core.model.NewsItemWithPicture
import core.transformers.NewsTransformer
import io.reactivex.Observable
import timber.log.Timber

/**
 * Data source for retrieving list of [NewsItemWithPicture]
 */
interface NewYorkTimesNewsDataSource {
  
  /**
   * Returns max amount of pages for list
   */
  val maxPages: Int
  
  /**
   * Returns list of [NewsItemWithPicture] wrapped as [Observable]
   */
  fun requestLatestNews(page: Int): Observable<List<NewsItemWithPicture>>
}

class NewYorkTimesNewsDataSourceImpl(
  private val webApi: WebApi,
  private val formatter: TimeFormatter,
  nytApiKey: String
) : NewYorkTimesNewsDataSource {
  
  private val baseUrl = "https://api.nytimes.com/svc/search/v2/" +
      "articlesearch.json?api-key=$nytApiKey&q=coronavirus&fq=headline:" +
      "(%22coronavirus%22%20%22covid-19%22%20%22covid%22)&sort=newest&page="
  
  override val maxPages: Int = 50
  
  override fun requestLatestNews(page: Int): Observable<List<NewsItemWithPicture>> {
    val url = baseUrl + page.toString()
    Timber.d("Loading page $page")
    return webApi.request(url).map { json -> NewsTransformer.toNewsItems(formatter, json) }
  }
}