package com.arsvechkarev.news.presentation

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.news.R
import com.arsvechkarev.news.di.NewsModuleInjector
import com.arsvechkarev.news.list.NewsAdapter
import core.BaseFragment
import kotlinx.android.synthetic.main.fragment_news.newsRecyclerView

class NewsFragment : BaseFragment(R.layout.fragment_news) {
  
  private var viewModel: NewsViewModel? = null
  
  private val adapter = NewsAdapter {}
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = NewsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this) {
        if (it is LoadedNews) {
          adapter.submitList(it.news)
        }
      }
      model.startLoadingData()
    }
    
    newsRecyclerView.adapter = adapter
    newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
  }
}