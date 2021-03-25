package core.model.mappers

import core.DateTimeFormatter
import core.Mapper
import core.model.data.NewsItem
import core.model.ui.NewsDifferentiableItem

class NewsItemsMapper(
  private val dateTimeFormatter: DateTimeFormatter
) : Mapper<List<NewsItem>, List<NewsDifferentiableItem>> {
  
  override fun map(value: List<NewsItem>) = ArrayList<NewsDifferentiableItem>().apply {
    value.forEach { newsItem ->
      add(NewsDifferentiableItem(
        id = newsItem.id,
        title = newsItem.title,
        description = newsItem.description,
        webUrl = newsItem.webUrl,
        formattedDate = dateTimeFormatter.formatPublishedDate(newsItem.publishedDate),
        imageUrl = newsItem.imageUrl
      ))
    }
  }
}