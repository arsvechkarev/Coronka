package com.arsvechkarev.news.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arsvechkarev.news.presentation.NewsFragment
import com.arsvechkarev.news.presentation.NewsViewModel
import com.arsvechkarev.news.presentation.list.NewsAdapter
import core.di.CoreComponent
import core.di.CoreComponent.networkAvailabilityNotifier
import core.di.CoreComponent.schedulers
import core.di.ModuleInterceptorManager.interceptModuleOrDefault
import core.model.ui.NewsDifferentiableItem

object NewsComponent {
  
  fun provideAdapter(
    fragment: NewsFragment,
    onNewsItemClicked: (NewsDifferentiableItem) -> Unit,
    onReadyToLoadNextPage: () -> Unit,
    onRetryItemClicked: () -> Unit,
  ): NewsAdapter {
    return NewsAdapter(fragment, CoreComponent.imageLoader, onNewsItemClicked,
      onReadyToLoadNextPage, onRetryItemClicked)
  }
  
  fun provideViewModel(fragment: NewsFragment): NewsViewModel {
    return ViewModelProvider(fragment, newsViewModelFactory).get(NewsViewModel::class.java)
  }
  
  private val newsViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      val module = interceptModuleOrDefault<NewsModule> { DefaultNewsModule }
      @Suppress("UNCHECKED_CAST")
      return NewsViewModel(module.newsUseCase, networkAvailabilityNotifier, schedulers) as T
    }
  }
}