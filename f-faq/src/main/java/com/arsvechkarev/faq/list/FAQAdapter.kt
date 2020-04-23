package com.arsvechkarev.faq.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arsvechkarev.faq.R
import com.arsvechkarev.faq.list.FAQAdapter.FAQViewHolder
import core.extenstions.inflate
import core.extenstions.rotateTo
import core.model.FAQItem
import kotlinx.android.synthetic.main.item_faq.view.arrowExpand
import kotlinx.android.synthetic.main.item_faq.view.expandableLayout
import kotlinx.android.synthetic.main.item_faq.view.itemFAQRoot
import kotlinx.android.synthetic.main.item_faq.view.layoutTitle
import kotlinx.android.synthetic.main.item_faq.view.textDescription
import kotlinx.android.synthetic.main.item_faq.view.textTitle

class FAQAdapter : RecyclerView.Adapter<FAQViewHolder>() {
  
  private var data: List<FAQItem> = ArrayList()
  private var expandedItems = ArrayList<Int>()
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
    return FAQViewHolder(parent.inflate(R.layout.item_faq))
  }
  
  override fun getItemCount(): Int {
    return data.size
  }
  
  override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
    holder.bind(data[position])
  }
  
  fun submitList(data: List<FAQItem>) {
    this.data = data
    notifyDataSetChanged()
  }
  
  inner class FAQViewHolder(itemView: View) : ViewHolder(itemView) {
    
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