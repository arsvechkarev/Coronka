package com.arsvechkarev.views.newsitem

import core.imageloading.LoadableImage

/**
 * Represents a view for displaying news
 */
interface NewsItemView : LoadableImage {
  
  fun setData(title: String, description: String, publishedDate: String)
}