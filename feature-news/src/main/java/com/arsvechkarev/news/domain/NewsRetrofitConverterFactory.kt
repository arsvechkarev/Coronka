package com.arsvechkarev.news.domain

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import core.DateTimeFormatter
import core.model.NewsItemWithPicture
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class NewsRetrofitConverterFactory(
  private val newsJsonConverter: NewsJsonConverter
) : Converter.Factory() {
  
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, List<NewsItemWithPicture>>? {
    val expectedType = TypeToken.getParameterized(List::class.java,
      NewsItemWithPicture::class.java).type
    if (type != expectedType) {
      return null
    }
    return Converter<ResponseBody, List<NewsItemWithPicture>> { value: ResponseBody ->
      newsJsonConverter.convert(value.string())
    }
  }
}

/** Converts json string to list of [NewsItemWithPicture] */
class NewsJsonConverter(private val dateTimeFormatter: DateTimeFormatter) {
  
  fun convert(json: String): List<NewsItemWithPicture> {
    val news = ArrayList<NewsItemWithPicture>()
    val outerObject = JsonParser.parseString(json).asJsonObject
    val array = outerObject.get(RESPONSE).asJsonObject.get(DOCS).asJsonArray
    for (i in 0 until array.size()) {
      val item = array.get(i).asJsonObject
      val id = item.get(ID).asString
      val title = item.get(HEADLINE).asJsonObject.get(MAIN).asString
      val description = item.get(LEAD_PARAGRAPH).asString
      val webUrl = item.get(WEB_URL).asString
      val date = item.get(PUB_DATE).asString
      val formattedDate = dateTimeFormatter.formatPublishedDate(date)
      val jsonArray = item.get(MULTIMEDIA).asJsonArray
      if (jsonArray.size() <= 10) continue
      val multimediaItem = jsonArray.get(10) ?: continue
      val imagePath = multimediaItem.asJsonObject.get(URL).asString
      val imageUrl = "$IMAGE_URL_PREFIX$imagePath"
      news.add(NewsItemWithPicture(id, title, description, webUrl, formattedDate, imageUrl))
    }
    return news
  }
  
  companion object {
    
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
  }
}