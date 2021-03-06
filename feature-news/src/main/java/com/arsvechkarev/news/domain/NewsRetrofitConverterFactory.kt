package com.arsvechkarev.news.domain

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import core.JsonConverter
import core.model.data.NewsItem
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NewsRetrofitConverterFactory(
  private val converter: JsonConverter<List<NewsItem>>
) : Converter.Factory() {
  
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, List<NewsItem>>? {
    val expectedType = TypeToken.getParameterized(List::class.java,
      NewsItem::class.java).type
    if (type != expectedType) {
      return null
    }
    return Converter { converter.convert(it.string()) }
  }
}

class NewsJsonConverter : JsonConverter<List<NewsItem>> {
  
  override fun convert(json: String): List<NewsItem> {
    val news = ArrayList<NewsItem>()
    val outerObject = JsonParser.parseString(json).asJsonObject
    val array = outerObject.get(RESPONSE).asJsonObject.get(DOCS).asJsonArray
    for (i in 0 until array.size()) {
      val item = array.get(i).asJsonObject
      val id = item.get(ID).asString
      val title = item.get(HEADLINE).asJsonObject.get(MAIN).asString
      val description = item.get(LEAD_PARAGRAPH).asString
      val webUrl = item.get(WEB_URL).asString
      val date = item.get(PUB_DATE).asString
      val jsonArray = item.get(MULTIMEDIA).asJsonArray
      if (jsonArray.size() <= 10) continue
      val multimediaItem = jsonArray.get(10) ?: continue
      val imagePath = multimediaItem.asJsonObject.get(URL).asString
      val imageUrl = "$IMAGE_URL_PREFIX$imagePath"
      news.add(NewsItem(id, title, description, webUrl, date, imageUrl))
    }
    return news
  }
  
  private companion object {
    
    const val RESPONSE = "response"
    const val DOCS = "docs"
    const val ID = "_id"
    const val HEADLINE = "headline"
    const val MAIN = "main"
    const val LEAD_PARAGRAPH = "lead_paragraph"
    const val WEB_URL = "web_url"
    const val PUB_DATE = "pub_date"
    const val MULTIMEDIA = "multimedia"
    const val URL = "url"
    const val IMAGE_URL_PREFIX = "https://static01.nyt.com/"
  }
}