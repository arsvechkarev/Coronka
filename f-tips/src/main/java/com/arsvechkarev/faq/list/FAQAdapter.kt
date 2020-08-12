package com.arsvechkarev.faq.list

import core.recycler.BaseAdapter

class FAQAdapter : BaseAdapter() {
  
  init {
    addDelegate(FAQHeaderAdapterDelegate())
    addDelegate(FAQItemAdapterDelegate())
  }
}