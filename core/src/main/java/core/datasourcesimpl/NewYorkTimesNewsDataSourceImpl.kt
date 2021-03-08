package core.datasourcesimpl

import core.TimeFormatter
import core.WebApi
import core.datasources.NewYorkTimesNewsDataSource
import core.jsontransformers.NewsTransformer.toNewsItems
import core.model.NewsItemWithPicture
import io.reactivex.Observable
import timber.log.Timber

class NewYorkTimesNewsDataSourceImpl(
  private val webApi: WebApi,
  private val formatter: TimeFormatter,
  nytApiKey: String
) : NewYorkTimesNewsDataSource {
  
  private val baseUrl = "https://api.nytimes.com/svc/search/v2/" +
      "articlesearch.json?api-key=$nytApiKey&q=coronavirus&fq=headline:" +
      "(%22coronavirus%22%20%22covid-19%22%20%22covid%22)&sort=newest&page="
  
  override fun requestLatestNews(page: Int): Observable<List<NewsItemWithPicture>> {
    val url = baseUrl + page.toString()
    Timber.d("Loading page $page")
    return webApi.request(url).map { json -> toNewsItems(formatter, json) }
  }
}