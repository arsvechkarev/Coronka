package com.arsvechkarev.views.newsitem

import core.imageloading.LoadableImage

interface NewsItemView : LoadableImage {
  
  fun setData(title: String, description: String, publishedDate: String)
}