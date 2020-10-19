package com.arsvechkarev.news.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.news.presentation.LoadingNextPage
import com.arsvechkarev.news.presentation.NewsFragment
import core.imageloading.GlideImageLoader
import core.imageloading.ImageLoader
import core.model.BasicNewsItem
import core.recycler.ListAdapter
import viewdsl.childView
import viewdsl.invisible
import viewdsl.visible

class NewsAdapter(
  fragment: NewsFragment,
  imageLoader: ImageLoader = GlideImageLoader,
  private var onNewsItemClicked: ((BasicNewsItem) -> Unit)? = null,
  onReadyToLoadNextPage: () -> Unit,
  private var onRetryItemClicked: (() -> Unit)? = null
) : ListAdapter(
  newsItemDelegate(fragment, imageLoader) { onNewsItemClicked?.invoke(it) },
  loadingNextPageDelegate { onRetryItemClicked?.invoke() },
  onReadyToLoadNextPage = onReadyToLoadNextPage
) {
  
  fun changeLoadingToError() {
    val itemView = lastHolderItemView()
    itemView?.childView(ProgressBar)?.invisible()
    itemView?.childView(FailureLayout)?.visible()
  }
  
  fun addLoadingItem(item: LoadingNextPage) {
    if (data.last() != item) addItem(item)
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    onNewsItemClicked = null
    onRetryItemClicked = null
  }
  
  private fun lastHolderItemView(): View? {
    return recyclerView?.findViewHolderForAdapterPosition(data.lastIndex)?.itemView
  }
  
  companion object {
    
    const val FailureLayout = "FailureLayout"
    const val ProgressBar = "ProgressBar"
    const val RetryButton = "ClickableTextView"
  }
}