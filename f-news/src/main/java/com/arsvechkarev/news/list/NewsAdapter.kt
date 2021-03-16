package com.arsvechkarev.news.list

import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.news.presentation.AdditionalItem
import com.arsvechkarev.news.presentation.NewsFragment
import core.imageloading.GlideImageLoader
import core.imageloading.ImageLoader
import core.model.BasicNewsItem
import core.recycler.ListAdapter

class NewsAdapter(
  fragment: NewsFragment,
  imageLoader: ImageLoader = GlideImageLoader,
  private var onNewsItemClicked: ((BasicNewsItem) -> Unit)? = null,
  onReadyToLoadNextPage: () -> Unit,
  private var onRetryItemClicked: (() -> Unit)? = null
) : ListAdapter(
  newsItemDelegate(fragment, imageLoader) { onNewsItemClicked?.invoke(it) },
  additionalItemDelegate { onRetryItemClicked?.invoke() },
  onReadyToLoadNextPage = onReadyToLoadNextPage
) {
  
  fun setLastItemAsError() {
    setLastItem(AdditionalItem(AdditionalItem.Mode.FAILURE))
  }
  
  fun setLastItemAsLoading() {
    setLastItem(AdditionalItem(AdditionalItem.Mode.LOADING))
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    onNewsItemClicked = null
    onRetryItemClicked = null
  }
  
  private fun setLastItem(state: AdditionalItem) {
    if (data.lastOrNull() is AdditionalItem) {
      data[data.lastIndex] = state
      notifyItemChanged(data.lastIndex)
    } else {
      addToEnd(state)
    }
  }
  
  companion object {
    
    const val FailureLayout = "FailureLayout"
    const val ProgressBar = "ProgressBar"
    const val RetryButton = "ClickableTextView"
  }
}