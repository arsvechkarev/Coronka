package com.arsvechkarev.common

import core.RxNetworker
import core.datetime.TimeFormatter
import core.model.NewsItemWithPicture
import core.recycler.DifferentiableItem
import io.reactivex.Observable
import org.json.JSONObject
import timber.log.Timber

class NewYorkTimesNewsRepository(
  private val rxNetworker: RxNetworker,
  private val formatter: TimeFormatter,
  nytApiKey: String
) {
  
  private val url = "https://api.nytimes.com/svc/search/v2/" +
      "articlesearch.json?api-key=$nytApiKey&q=coronavirus&fq=headline:" +
      "(%22coronavirus%22%20%22covid-19%22%20%22covid%22)&sort=newest"
  
  fun getLatestNews(): Observable<List<DifferentiableItem>> {
    return rxNetworker.requestObservable(url)
        .map { transformJson(it) }
  }
  
  private fun transformJson(json: String): List<DifferentiableItem> {
    val news = ArrayList<DifferentiableItem>()
    val outerObject = JSONObject(json)
    val array = outerObject.getJSONObject("response").getJSONArray("docs")
    for (i in 0 until array.length()) {
      val item = array.getJSONObject(i)
      val title = item.getJSONObject("headline").getString("main")
      val description = item.getString("lead_paragraph")
      val webUrl = item.getString("web_url")
      val date = item.getString("pub_date")
      val formattedDate = formatter.formatPublishedDate(date)
      val multimediaItem = item.getJSONArray("multimedia").optJSONObject(10)
      if (multimediaItem != null) {
        val imagePath = multimediaItem.getString("url")
        val imageUrl = "https://static01.nyt.com/$imagePath"
        Timber.d("$i: title='$title', date='$date', imageUrl='$imageUrl'")
        news.add(NewsItemWithPicture(i, title, description, webUrl, formattedDate, imageUrl))
      }
    }
    return news
  }
}