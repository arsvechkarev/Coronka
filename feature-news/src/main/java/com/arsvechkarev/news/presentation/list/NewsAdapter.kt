package com.arsvechkarev.news.presentation.list

import androidx.recyclerview.widget.RecyclerView
import api.threading.Threader
import com.arsvechkarev.news.presentation.AdditionalItem
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.recycler.ListAdapter
import core.ImageLoader
import core.model.BasicNewsItem

class NewsAdapter(
  fragment: NewsFragment,
  imageLoader: ImageLoader,
  threader: Threader,
  private var onNewsItemClicked: ((BasicNewsItem) -> Unit)? = null,
  onReadyToLoadNextPage: () -> Unit,
  private var onRetryItemClicked: (() -> Unit)? = null
) : ListAdapter(threader, onReadyToLoadNextPage) {
  
  init {
    addDelegates(
      newsItemDelegate(fragment, imageLoader) { onNewsItemClicked?.invoke(it) },
      additionalItemDelegate { onRetryItemClicked?.invoke() },
    )
  }
  
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