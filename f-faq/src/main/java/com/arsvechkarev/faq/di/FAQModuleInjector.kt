package com.arsvechkarev.faq.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arsvechkarev.faq.presentation.FAQFragment
import com.arsvechkarev.faq.presentation.FAQViewModel
import com.arsvechkarev.faq.repository.FAQLoader
import core.Application

object FAQModuleInjector {
  
  fun provideViewModel(fragment: FAQFragment): FAQViewModel {
    val loader = FAQLoader(fragment.resources)
    val factory = faqViewModelFactory(Application.Threader, loader)
    return ViewModelProviders.of(fragment, factory).get(FAQViewModel::class.java)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun faqViewModelFactory(
    threader: Application.Threader,
    loader: FAQLoader
  ) = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      val viewModel = FAQViewModel(threader, loader)
      return viewModel as T
    }
  }
}