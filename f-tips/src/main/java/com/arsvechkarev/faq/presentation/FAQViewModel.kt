package com.arsvechkarev.faq.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.faq.repository.FAQLoader
import core.concurrency.Threader
import core.model.FAQHeader
import core.recycler.DisplayableItem

class FAQViewModel(
  private val threader: Threader,
  private val loader: FAQLoader
) : ViewModel() {
  
  private val _data = MutableLiveData<List<DisplayableItem>>()
  val data: LiveData<List<DisplayableItem>>
    get() = _data
  
  fun loadData() {
    threader.onIoThread {
      val list = ArrayList<DisplayableItem>(27)
      list.add(FAQHeader)
      loader.populateList(list)
      threader.onMainThread { _data.value = list }
    }
  }
}