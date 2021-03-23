package base.views.newsitem

import core.LoadableImage

/**
 * Represents a view for displaying news
 */
interface NewsItemView : LoadableImage {
  
  fun setData(title: String, description: String, publishedDate: String)
}