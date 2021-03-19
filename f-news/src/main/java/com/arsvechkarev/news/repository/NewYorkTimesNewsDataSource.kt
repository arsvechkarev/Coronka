package com.arsvechkarev.news.repository

import core.TimeFormatter
import core.WebApi
import core.model.NewsItemWithPicture
import io.reactivex.Maybe
import io.reactivex.Observable
import okhttp3.HttpUrl
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
  fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>>
}

class NewYorkTimesNewsDataSourceImpl(
  private val webApi: WebApi,
  private val formatter: TimeFormatter,
  private val nytApiKey: String
) : NewYorkTimesNewsDataSource {
  
  override val maxPages: Int = 50
  
  override fun requestLatestNews(page: Int): Maybe<List<NewsItemWithPicture>> {
    if (page >= maxPages) {
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
    
    Timber.d("Loading page $page")
    return webApi.request(url).map { json -> NewsTransformer.toNewsItems(formatter, json) }
        .toMaybe()
  }
}