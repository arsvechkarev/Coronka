package com.arsvechkarev.news.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import base.BaseFragment
import base.behaviors.HeaderBehavior
import base.behaviors.ScrollingRecyclerBehavior
import base.behaviors.ViewUnderHeaderBehavior
import base.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import base.drawables.GradientHeaderDrawable
import base.drawables.NewsListLoadingStub
import base.extensions.getMessageRes
import base.hostActivity
import base.resources.Dimens.ErrorLayoutImageSize
import base.resources.Dimens.ErrorLayoutTextPadding
import base.resources.Dimens.GradientHeaderHeight
import base.resources.Dimens.ImageDrawerMargin
import base.resources.Styles.BoldTextView
import base.resources.Styles.HeaderTextView
import base.resources.TextSizes
import base.views.RetryButton
import com.arsvechkarev.news.R
import com.arsvechkarev.news.di.NewsComponent
import com.arsvechkarev.recycler.CallbackType
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
import core.BaseScreenState
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.FailureReason.UNKNOWN
import core.Loading
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
        tag(LayoutFailure)
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
          onClick { viewModel.retryLoadingData() }
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
  
  private lateinit var viewModel: NewsViewModel
  
  private val newsAdapter = NewsComponent.provideAdapter(this,
    onNewsItemClicked = { newsItem ->
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse(newsItem.webUrl)
      startActivity(intent)
    },
    onReadyToLoadNextPage = {
      viewModel.tryLoadNextPage()
    },
    onRetryItemClicked = {
      viewModel.retryLoadingNextPage()
    }
  )
  
  override fun onInit() {
    viewModel = NewsComponent.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    hostActivity.enableTouchesOnDrawer()
    Timber.d("Logggging NewsFragment onInit")
  }
  
  override fun onHiddenChanged(hidden: Boolean) {
    Timber.d("Logggging NewsFragment onHiddenChanged $hidden")
    if (hidden) hostActivity.enableTouchesOnDrawer()
  }
  
  override fun onOrientationBecameLandscape() {
    view(ImageFailure).gone()
  }
  
  override fun onOrientationBecamePortrait() {
    view(ImageFailure).visible()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> renderLoading()
      is LoadedNews -> renderLoadedNews(state)
      is LoadingNextPage -> renderLoadingNextPage()
      is LoadedNextPage -> renderLoadedNextPage(state)
      is FailureLoadingNextPage -> renderFailureLoadingNextPage()
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderLoading() {
    view(LoadingLayout).animateVisible()
    animateInvisible(view(RecyclerView), view(LayoutFailure))
  }
  
  private fun renderLoadedNews(state: LoadedNews) {
    view(LoadingLayout).animateInvisible()
    view(RecyclerView).animateVisible()
    newsAdapter.submitList(state.news, CallbackType.TWO_LISTS)
  }
  
  private fun renderLoadingNextPage() {
    view!!.post { // Posting in case recycler is currently computing layout
      newsAdapter.setLastItemAsLoading()
    }
  }
  
  private fun renderLoadedNextPage(state: LoadedNextPage) {
    newsAdapter.removeLastAndAdd(state.newNews)
  }
  
  private fun renderFailureLoadingNextPage() {
    newsAdapter.setLastItemAsError()
  }
  
  private fun renderFailure(state: Failure) {
    Timber.e(state.throwable, "Error")
    view(LoadingLayout).animateInvisible()
    view(LayoutFailure).animateVisible()
    textView(ErrorMessage).text(state.reason.getMessageRes())
    when (state.reason) {
      TIMEOUT, UNKNOWN -> imageView(ImageFailure).image(R.drawable.image_unknown_error)
      NO_CONNECTION -> imageView(ImageFailure).image(R.drawable.image_no_connection)
    }
  }
  
  override fun onAttach(context: Context) {
    super.onAttach(context)
    Timber.d("Logggging NewsFragment onAttach")
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.d("Logggging NewsFragment onCreate")
  }
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    Timber.d("Logggging NewsFragment onCreateView")
    return super.onCreateView(inflater, container, savedInstanceState)
  }
  
  override fun onStart() {
    super.onStart()
    Timber.d("Logggging NewsFragment onStart")
  }
  
  override fun onResume() {
    super.onResume()
    Timber.d("Logggging NewsFragment onResume")
  }
  
  override fun onPause() {
    super.onPause()
    Timber.d("Logggging NewsFragment onPause")
  }
  
  override fun onStop() {
    super.onStop()
    Timber.d("Logggging NewsFragment onStop")
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    Timber.d("Logggging NewsFragment onDestroyView")
  }
  
  override fun onDestroy() {
    super.onDestroy()
    Timber.d("Logggging NewsFragment onDestroy")
  }
  
  override fun onDetach() {
    super.onDetach()
    Timber.d("Logggging NewsFragment onDetach")
  }
  
  companion object {
    
    const val LoadingLayout = "NewsLoadingLayout"
    const val LayoutFailure = "NewsErrorLayout"
    const val RecyclerView = "NewsRecyclerView"
    const val ImageFailure = "NewsImageFailure"
    const val ErrorMessage = "TextErrorMessage"
    const val ButtonRetry = "ButtonRetry"
  }
}
