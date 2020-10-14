package core.model

import core.recycler.DifferentiableItem

/**
 * News item with basic information
 *
 * @param webUrl Source url of the article
 * @param publishedDate Date when the article was published, i.e "2020-10-12T18:51:03+0000"
 */
open class BasicNewsItem(
  override val id: Int,
  val title: String,
  val description: String,
  val webUrl: String,
  val publishedDate: String
) : DifferentiableItem {
  
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is BasicNewsItem) return false
    
    if (id != other.id) return false
    if (title != other.title) return false
    if (webUrl != other.webUrl) return false
    if (publishedDate != other.publishedDate) return false
    
    return true
  }
  
  override fun hashCode(): Int {
    var result = id
    result = 31 * result + title.hashCode()
    result = 31 * result + webUrl.hashCode()
    result = 31 * result + publishedDate.hashCode()
    return result
  }
}

/**
 * @param imageUrl Image url for the article
 */
class NewsItemWithPicture(
  override val id: Int,
  title: String,
  description: String,
  webUrl: String,
  publishedDate: String,
  val imageUrl: String,
) : BasicNewsItem(id, title, description, webUrl, publishedDate)