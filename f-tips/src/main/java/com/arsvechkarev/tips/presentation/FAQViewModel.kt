package com.arsvechkarev.tips.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arsvechkarev.tips.repository.FAQLoader
import core.concurrency.Threader
import core.recycler.SortableDisplayableItem

class FAQViewModel(
  private val threader: Threader,
  private val loader: FAQLoader
) : ViewModel() {
  
  private val _data = MutableLiveData<List<SortableDisplayableItem>>()
  val data: LiveData<List<SortableDisplayableItem>>
    get() = _data
  
  fun loadData() {
    threader.onIoThread {
      val list = ArrayList<SortableDisplayableItem>(27)
      //      list.add(FAQHeader)
      loader.populateList(list)
      threader.onMainThread { _data.value = list }
    }
  }
}