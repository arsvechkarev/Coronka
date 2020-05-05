package com.arsvechkarev.faq.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.faq.presentation.FAQFragment
import com.arsvechkarev.faq.presentation.FAQViewModel
import com.arsvechkarev.faq.repository.FAQLoader
import core.concurrency.AndroidThreader

object FAQModuleInjector {
  
  fun provideViewModel(fragment: FAQFragment): FAQViewModel {
    val loader = FAQLoader(fragment.resources)
    val factory = faqViewModelFactory(loader)
    return ViewModelProviders.of(fragment, factory).get(FAQViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun faqViewModelFactory(
    loader: FAQLoader
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = FAQViewModel(AndroidThreader, loader)
      return viewModel as T
    }
  }
}