package com.arsvechkarev.news.domain

import core.DateTimeFormatter
import core.model.NewsItemWithPicture
import org.json.JSONObject

object NewsTransformer {
  
  private const val RESPONSE = "response"
  private const val DOCS = "docs"
  private const val ID = "_id"
  private const val HEADLINE = "headline"
  private const val MAIN = "main"
  private const val LEAD_PARAGRAPH = "lead_paragraph"
  private const val WEB_URL = "web_url"
  private const val PUB_DATE = "pub_date"
  private const val MULTIMEDIA = "multimedia"
  private const val URL = "url"
  private const val IMAGE_URL_PREFIX = "https://static01.nyt.com/"
  
  fun toNewsItems(formatter: DateTimeFormatter, json: String): List<NewsItemWithPicture> {
    val news = ArrayList<NewsItemWithPicture>()
    val outerObject = JSONObject(json)
    val array = outerObject.getJSONObject(RESPONSE).getJSONArray(DOCS)
    for (i in 0 until array.length()) {
      val item = array.getJSONObject(i)
      val id = item.getString(ID)
      val title = item.getJSONObject(HEADLINE).getString(MAIN)
      val description = item.getString(LEAD_PARAGRAPH)
      val webUrl = item.getString(WEB_URL)
      val date = item.getString(PUB_DATE)
      val formattedDate = formatter.formatPublishedDate(date)
      val multimediaItem = item.getJSONArray(MULTIMEDIA).optJSONObject(10)
      if (multimediaItem != null) {
        val imagePath = multimediaItem.getString(URL)
        val imageUrl = "$IMAGE_URL_PREFIX$imagePath"
        news.add(NewsItemWithPicture(id, title, description, webUrl, formattedDate, imageUrl))
      }
    }
    return news
  }
}