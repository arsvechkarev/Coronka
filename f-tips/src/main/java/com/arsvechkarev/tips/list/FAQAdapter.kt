package com.arsvechkarev.tips.list

import core.recycler.BaseAdapter

class FAQAdapter : BaseAdapter() {
  
  init {
    addDelegate(FAQHeaderAdapterDelegate())
    addDelegate(FAQItemAdapterDelegate())
  }
}