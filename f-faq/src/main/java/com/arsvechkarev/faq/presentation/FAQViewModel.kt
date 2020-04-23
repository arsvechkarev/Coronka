package com.arsvechkarev.faq.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.faq.repository.FAQRepository
import core.Application
import core.model.FAQItem

class FAQViewModel(
  private val threader: Application.Threader,
  private val repository: FAQRepository
) : ViewModel() {
  
  private val _data = MutableLiveData<List<FAQItem>>()
  val data: LiveData<List<FAQItem>>
    get() = _data
  
  fun loadData() {
    threader.ioWorker.submit {
      val list = repository.loadFAQList()
      threader.mainThreadWorker.submit { _data.value = list }
    }
  }
}