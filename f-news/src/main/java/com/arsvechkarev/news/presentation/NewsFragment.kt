package com.arsvechkarev.news.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.news.R
import com.arsvechkarev.news.di.NewsModuleInjector
import com.arsvechkarev.news.list.NewsAdapter
import com.arsvechkarev.views.ClickableTextView
import com.arsvechkarev.views.behaviors.HeaderBehavior
import com.arsvechkarev.views.behaviors.ScrollingRecyclerBehavior
import com.arsvechkarev.views.behaviors.ViewUnderHeaderBehavior
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.applyLoadingDrawable
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import com.arsvechkarev.views.drawables.NewsListLoadingStub
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Loading
import core.hostActivity
import core.viewbuilding.Styles.HeaderTextView
import core.viewbuilding.Styles.RetryTextView
import core.viewbuilding.TextSizes
import timber.log.Timber
import viewdsl.Ints.dp
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.Size.IntSize
import viewdsl.animateInvisible
import viewdsl.animateVisible
import viewdsl.background
import viewdsl.behavior
import viewdsl.buildView
import viewdsl.gravity
import viewdsl.image
import viewdsl.invisible
import viewdsl.layoutGravity
import viewdsl.marginVertical
import viewdsl.margins
import viewdsl.onClick
import viewdsl.orientation
import viewdsl.tag
import viewdsl.text
import viewdsl.textSize

class NewsFragment : BaseFragment() {
  
  private var viewModel: NewsViewModel? = null
  
  private val newsAdapter = NewsAdapter(this,
    onNewsItemClicked = { newsItem ->
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse(newsItem.webUrl)
      startActivity(intent)
    },
    onReadyToLoadNextPage = {
      viewModel?.tryLoadNextPage()
    },
    onRetryItemClicked = {
      viewModel?.tryLoadNextPage()
    }
  )
  
  override fun buildLayout() = buildView {
    CoordinatorLayout(MatchParent, MatchParent) {
      child<View>(MatchParent, MatchParent) {
        tag(NewsLoadingLayout)
        behavior(ViewUnderHeaderBehavior())
        applyLoadingDrawable(NewsListLoadingStub())
      }
      child<LinearLayout>(MatchParent, MatchParent) {
        tag(NewsErrorLayout)
        invisible()
        gravity(CENTER)
        orientation(LinearLayout.VERTICAL)
        behavior(ViewUnderHeaderBehavior())
        child<TextView>(WrapContent, WrapContent) {
          tag(TextErrorMessage)
          textSize(TextSizes.H3)
        }
        child<ImageView>(IntSize(120.dp), IntSize(120.dp)) {
          tag(NewsImageFailure)
          marginVertical(24.dp)
          image(R.drawable.image_unknown_error)
        }
        child<ClickableTextView>(WrapContent, WrapContent, style = RetryTextView) {
          text(R.string.text_retry)
          textSize(TextSizes.H3)
          onClick { viewModel?.startLoadingData() }
        }
      }
      child<RecyclerView>(MatchParent, MatchParent) {
        tag(NewsRecyclerView)
        invisible()
        behavior(ScrollingRecyclerBehavior())
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      }
      child<FrameLayout>(MatchParent, IntSize(120.dp)) {
        behavior(HeaderBehavior(context))
        child<View>(MatchParent, MatchParent) {
          background(GradientHeaderDrawable())
        }
        child<TextView>(WrapContent, WrapContent, style = HeaderTextView) {
          text(R.string.title_tips)
          layoutGravity(CENTER)
        }
        child<ImageView>(WrapContent, WrapContent) {
          margins(left = 16.dp, top = 16.dp)
          image(R.drawable.ic_drawer)
          onClick { hostActivity.openDrawer() }
        }
      }
    }
  }
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = NewsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is LoadingNextPage -> renderLoadingNextPage(state)
      is Loading -> renderLoading()
      is LoadedNews -> renderLoadedNews(state)
      is LoadedNextPage -> renderLoadedNextPage(state)
      is FailureLoadingNextPage -> renderFailureLoadingNextPage()
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderLoadingNextPage(state: LoadingNextPage) {
    newsAdapter.addLoadingItem(state)
  }
  
  private fun renderLoading() {
    view(NewsLoadingLayout).animateVisible()
    animateInvisible(view(NewsRecyclerView), view(NewsErrorLayout))
  }
  
  private fun renderLoadedNews(state: LoadedNews) {
    view(NewsLoadingLayout).animateInvisible()
    view(NewsRecyclerView).animateVisible()
    newsAdapter.submitList(state.news)
  }
  
  private fun renderLoadedNextPage(state: LoadedNextPage) {
    newsAdapter.removeLastAndAdd(state.news)
  }
  
  private fun renderFailureLoadingNextPage() {
    newsAdapter.changeLoadingToError()
  }
  
  private fun renderFailure(state: Failure) {
    Timber.e(state.throwable, "Error")
    view(NewsLoadingLayout).animateInvisible()
    view(NewsErrorLayout).animateVisible()
  }
  
  private companion object {
    const val NewsLoadingLayout = "NewsLoadingLayout"
    const val NewsErrorLayout = "NewsErrorLayout"
    const val NewsRecyclerView = "NewsRecyclerView"
    const val NewsImageFailure = "NewsImageFailure"
    const val TextErrorMessage = "TextErrorMessage"
  }
}