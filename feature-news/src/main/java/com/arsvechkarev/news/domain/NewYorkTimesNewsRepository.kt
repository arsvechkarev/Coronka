package com.arsvechkarev.news.domain

import core.DateTimeFormatter
import core.WebApi
import core.model.NewsItemWithPicture
import io.reactivex.Maybe
import okhttp3.HttpUrl
import timber.log.Timber

/**
 * Data source for retrieving list of [NewsItemWithPicture]
 */
interface NewYorkTimesNewsRepository {
  
  /**
   * Returns max amount of pages for list
   */
  val maxPagesCount: Int
  
  /**
   * Returns list of [NewsItemWithPicture] wrapped as [Maybe]
   */
  fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>>
}

class NewYorkTimesNewsDataRepositoryImpl(
  private val webApi: WebApi,
  private val formatter: DateTimeFormatter,
  private val nytApiKey: String
) : NewYorkTimesNewsRepository {
  
  override val maxPagesCount: Int = 50
  
  override fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>> {
    Timber.d("Loading page $page")
    if (page > maxPagesCount) {
      return Maybe.empty()
    }
    val url = HttpUrl.Builder()
        .scheme("https")
        .host("api.nytimes.com")
        .addPathSegments("svc/search/v2/articlesearch.json")
        .addQueryParameter("api-key", nytApiKey)
        .addQueryParameter("q", "coronavirus")
        .addQueryParameter("fq", "headline:(\"coronavirus\" \"covid-19\" \"covid\")")
        .addQueryParameter("sort", "newest")
        .addQueryParameter("page", page.toString())
        .build()
        .toString()
    return webApi.request(url).map { json -> NewsTransformer.toNewsItems(formatter, json) }
        .toMaybe()
  }
}