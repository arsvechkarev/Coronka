package com.arsvechkarev.news.domain

import core.model.data.NewsItem
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Api for requesting news
 *
 * @see NewsJsonConverter
 * @see NewsRetrofitConverterFactory
 */
interface NewsApi {
  
  /**
   * Returns list of [NewsItem] with params from [queryMap]
   */
  @GET("/svc/search/v2/articlesearch.json")
  fun requestLatestNews(@QueryMap queryMap: Map<String, String>): Single<List<NewsItem>>
}