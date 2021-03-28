package com.arsvechkarev.news.presentation.list

import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.recycler.ListAdapter
import core.ImageLoader
import core.model.ui.NewsDifferentiableItem

class NewsAdapter(
  fragment: NewsFragment,
  imageLoader: ImageLoader,
  private var onNewsItemClicked: ((NewsDifferentiableItem) -> Unit)? = null,
  onReadyToLoadNextPage: () -> Unit,
  private var onRetryItemClicked: (() -> Unit)? = null
) : ListAdapter(onReadyToLoadNextPage) {
  
  init {
    addDelegates(
      newsItemDelegate(fragment, imageLoader) { onNewsItemClicked?.invoke(it) },
      additionalItemDelegate { onRetryItemClicked?.invoke() },
    )
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    onNewsItemClicked = null
    onRetryItemClicked = null
  }
  
  companion object {
  
    const val LayoutFailure = "FailureLayout"
    const val ProgressBar = "ProgressBar"
    const val RetryButton = "ClickableTextView"
  }
}