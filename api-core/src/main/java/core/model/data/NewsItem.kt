package core.model.data

import androidx.annotation.Keep

@Keep
data class NewsItem(
  val id: String,
  val title: String,
  val description: String,
  val webUrl: String,
  val publishedDate: String,
  val imageUrl: String,
)