package com.arsvechkarev.news.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.news.R
import com.arsvechkarev.news.di.NewsModuleInjector
import com.arsvechkarev.news.list.NewsAdapter
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.applyLoadingDrawable
import com.arsvechkarev.views.drawables.NewsListLoadingStub
import com.arsvechkarev.views.drawables.createGradientHeaderDrawable
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Loading
import core.hostActivity
import kotlinx.android.synthetic.main.fragment_news.newsGradientHeaderView
import kotlinx.android.synthetic.main.fragment_news.newsImageDrawer
import kotlinx.android.synthetic.main.fragment_news.newsLoadingStub
import kotlinx.android.synthetic.main.fragment_news.newsRecyclerView
import timber.log.Timber
import viewdsl.animateInvisible
import viewdsl.animateVisible

class NewsFragment : BaseFragment(R.layout.fragment_news) {
  
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
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = NewsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    initClickListeners()
    initDrawables()
    newsRecyclerView.apply {
      adapter = newsAdapter
      layoutManager = LinearLayoutManager(requireContext())
      addItemDecoration(DividerItemDecoration(requireContext(),
        DividerItemDecoration.VERTICAL))
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
    newsLoadingStub.animateVisible()
    newsRecyclerView.animateInvisible()
  }
  
  private fun renderLoadedNews(state: LoadedNews) {
    newsLoadingStub.animateInvisible()
    newsRecyclerView.animateVisible()
    newsAdapter.submitList(state.news)
  }
  
  private fun renderLoadedNextPage(state: LoadedNextPage) {
    newsAdapter.removeLastAndAdd(state.news)
  }
  
  private fun renderFailureLoadingNextPage() {
    newsAdapter.changeLoadingToError()
  }
  
  private fun renderFailure(state: Failure) {
    newsLoadingStub.animateInvisible()
    Timber.e(state.throwable, "Error")
  }
  
  private fun initClickListeners() {
    newsImageDrawer.setOnClickListener { hostActivity.openDrawer() }
  }
  
  private fun initDrawables() {
    newsLoadingStub.applyLoadingDrawable(NewsListLoadingStub())
    newsGradientHeaderView.background = createGradientHeaderDrawable()
  }
}