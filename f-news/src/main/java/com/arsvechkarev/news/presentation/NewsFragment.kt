package com.arsvechkarev.news.presentation

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
import core.extenstions.animateInvisible
import core.extenstions.animateVisible
import core.hostActivity
import kotlinx.android.synthetic.main.fragment_news.newsGradientHeaderView
import kotlinx.android.synthetic.main.fragment_news.newsImageDrawer
import kotlinx.android.synthetic.main.fragment_news.newsLoadingStub
import kotlinx.android.synthetic.main.fragment_news.newsRecyclerView

class NewsFragment : BaseFragment(R.layout.fragment_news) {
  
  private var viewModel: NewsViewModel? = null
  
  private val newsAdapter = NewsAdapter(this, onNewsItemClicked = {
  
  })
  
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
      is Loading -> renderLoading()
      is LoadedNews -> renderLoadedNews(state)
      is Failure -> renderFailure(state)
    }
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
  
  private fun renderFailure(state: Failure) {
  
  }
  
  private fun initClickListeners() {
    newsImageDrawer.setOnClickListener { hostActivity.openDrawer() }
  }
  
  private fun initDrawables() {
    newsLoadingStub.applyLoadingDrawable(NewsListLoadingStub())
    newsGradientHeaderView.background = createGradientHeaderDrawable()
  }
}