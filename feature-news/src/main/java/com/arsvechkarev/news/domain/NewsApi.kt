package com.arsvechkarev.news.domain

import core.model.NewsItemWithPicture
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
   * Returns list of [NewsItemWithPicture] with params from [queryMap]
   */
  @GET("/svc/search/v2/articlesearch.json")
  fun requestLatestNews(@QueryMap queryMap: Map<String, String>): Single<List<NewsItemWithPicture>>
}