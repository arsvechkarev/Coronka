package com.arsvechkarev.faq.list

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import core.extenstions.dpInt
import core.model.FAQHeader
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem

class FAQHeaderAdapterDelegate : AdapterDelegate(FAQHeader::class) {
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    val textView = TextView(parent.context).apply {
      text = "Blah blah blah"
      textSize = 50f
      setPadding(16.dpInt, 16.dpInt, 16.dpInt, 16.dpInt)
    }
    return object : RecyclerView.ViewHolder(textView) {}
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
  }
}