package com.arsvechkarev.tips.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arsvechkarev.tips.R
import core.extenstions.inflate
import core.extenstions.rotateTo
import core.model.FAQItem
import core.recycler.AdapterDelegate
import core.recycler.DisplayableItem
import kotlinx.android.synthetic.main.item_faq.view.arrowExpand
import kotlinx.android.synthetic.main.item_faq.view.expandableLayout
import kotlinx.android.synthetic.main.item_faq.view.itemFAQRoot
import kotlinx.android.synthetic.main.item_faq.view.layoutTitle
import kotlinx.android.synthetic.main.item_faq.view.textDescription
import kotlinx.android.synthetic.main.item_faq.view.textTitle

class FAQItemAdapterDelegate : AdapterDelegate(FAQItem::class) {
  
  private var expandedItems = ArrayList<Int>()
  private var recyclerView: RecyclerView? = null
  
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    this.recyclerView = recyclerView
  }
  
  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return FAQViewHolder(parent.inflate(R.layout.item_faq))
  }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: DisplayableItem) {
    (holder as FAQViewHolder).bind(item as FAQItem)
  }
  
  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    this.recyclerView = null
  }
  
  inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    init {
      itemView.itemFAQRoot.setOnClickListener { handleClick() }
      itemView.layoutTitle.setOnClickListener { handleClick() }
    }
    
    fun bind(item: FAQItem) {
      if (expandedItems.contains(adapterPosition)) {
        itemView.expandableLayout.visible(false)
        itemView.arrowExpand.rotation = 180f
      } else {
        itemView.expandableLayout.gone(false)
        itemView.arrowExpand.rotation = 0f
      }
      itemView.textTitle.text = item.title
      itemView.textDescription.text = item.description
    }
    
    private fun handleClick() {
      if (expandedItems.contains(adapterPosition)) {
        expandedItems.remove(adapterPosition)
        itemView.expandableLayout.gone()
        itemView.arrowExpand.rotateTo(0f)
      } else {
        expandedItems.add(adapterPosition)
        itemView.expandableLayout.visible()
        itemView.arrowExpand.rotateTo(180f)
      }
    }
  }
}