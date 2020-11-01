package com.arsvechkarev.news.presentation

import android.content.Intent
import android.net.Uri
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
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.Size.IntSize
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.behavior
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.visible
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.RetryButton
import com.arsvechkarev.views.behaviors.HeaderBehavior
import com.arsvechkarev.views.behaviors.ScrollingRecyclerBehavior
import com.arsvechkarev.views.behaviors.ViewUnderHeaderBehavior
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import com.arsvechkarev.views.drawables.NewsListLoadingStub
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Failure.FailureReason.NO_CONNECTION
import core.Failure.FailureReason.TIMEOUT
import core.Failure.FailureReason.UNKNOWN
import core.Loading
import core.hostActivity
import core.viewbuilding.Dimens.ErrorLayoutImageSize
import core.viewbuilding.Dimens.ErrorLayoutTextPadding
import core.viewbuilding.Dimens.GradientHeaderHeight
import core.viewbuilding.Dimens.ImageDrawerMargin
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.Styles.HeaderTextView
import core.viewbuilding.TextSizes
import timber.log.Timber

class NewsFragment : BaseFragment() {
  
  override fun buildLayout() = withViewBuilder {
    CoordinatorLayout(MatchParent, MatchParent) {
      child<View>(MatchParent, MatchParent) {
        tag(LoadingLayout)
        behavior(ViewUnderHeaderBehavior())
        setLoadingDrawable(NewsListLoadingStub())
      }
      child<RecyclerView>(MatchParent, MatchParent) {
        tag(RecyclerView)
        invisible()
        behavior(ScrollingRecyclerBehavior())
        adapter = newsAdapter
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      }
      child<LinearLayout>(MatchParent, MatchParent) {
        tag(ErrorLayout)
        invisible()
        gravity(CENTER)
        orientation(LinearLayout.VERTICAL)
        behavior(ViewUnderHeaderBehavior())
        child<ImageView>(ErrorLayoutImageSize, ErrorLayoutImageSize) {
          tag(ImageFailure)
          image(R.drawable.image_unknown_error)
          margins(bottom = ErrorLayoutTextPadding)
        }
        child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
          tag(ErrorMessage)
          gravity(CENTER)
          paddings(
            start = ErrorLayoutTextPadding,
            end = ErrorLayoutTextPadding,
            bottom = ErrorLayoutTextPadding
          )
          textSize(TextSizes.H2)
        }
        child<RetryButton>(WrapContent, WrapContent) {
          tag(ButtonRetry)
          onClick { viewModel?.startLoadingData() }
        }
      }
      child<FrameLayout>(MatchParent, IntSize(GradientHeaderHeight + StatusBarHeight)) {
        behavior(HeaderBehavior(context))
        child<View>(MatchParent, MatchParent) {
          paddings(top = StatusBarHeight)
          background(GradientHeaderDrawable())
        }
        child<TextView>(WrapContent, WrapContent, style = HeaderTextView) {
          text(R.string.label_news)
          layoutGravity(CENTER)
        }
        child<ImageView>(WrapContent, WrapContent) {
          margins(start = ImageDrawerMargin, top = ImageDrawerMargin + StatusBarHeight)
          image(R.drawable.ic_drawer)
          onClick { hostActivity.openDrawer() }
        }
      }
    }
  }
  
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
  
  override fun onInit() {
    viewModel = NewsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
  }
  
  override fun onNetworkAvailable() {
    if (viewModel != null && viewModel?.state?.value is Failure) {
      viewModel!!.startLoadingData()
    }
  }
  
  override fun onOrientationBecameLandscape() {
    view(ImageFailure).gone()
  }
  
  override fun onOrientationBecamePortrait() {
    view(ImageFailure).visible()
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
    view(LoadingLayout).animateVisible()
    animateInvisible(view(RecyclerView), view(ErrorLayout))
  }
  
  private fun renderLoadedNews(state: LoadedNews) {
    view(LoadingLayout).animateInvisible()
    view(RecyclerView).animateVisible()
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
    view(LoadingLayout).animateInvisible()
    view(ErrorLayout).animateVisible()
    textView(ErrorMessage).text(state.reason.getStringRes())
    when (state.reason) {
      TIMEOUT, UNKNOWN -> imageView(ImageFailure).image(R.drawable.image_unknown_error)
      NO_CONNECTION -> imageView(ImageFailure).image(R.drawable.image_no_connection)
    }
  }
  
  private companion object {
  
    const val LoadingLayout = "NewsLoadingLayout"
    const val ErrorLayout = "NewsErrorLayout"
    const val RecyclerView = "NewsRecyclerView"
    const val ImageFailure = "NewsImageFailure"
    const val ErrorMessage = "TextErrorMessage"
    const val ButtonRetry = "ButtonRetry"
  }
}