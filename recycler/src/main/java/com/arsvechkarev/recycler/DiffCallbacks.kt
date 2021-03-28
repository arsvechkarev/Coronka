package com.arsvechkarev.recycler

import androidx.recyclerview.widget.DiffUtil
import api.recycler.DifferentiableItem
import api.recycler.DisplayableItem

/**
 * DiffCallback that always returns false for every item
 */
class AlwaysFalseCallback(
  private val oldList: List<DisplayableItem>,
  private val newList: List<DisplayableItem>,
) : DiffUtil.Callback() {
  
  override fun getOldListSize() = oldList.size
  
  override fun getNewListSize() = newList.size
  
  override fun areItemsTheSame(oldPosition: Int, newPosition: Int) = false
  
  override fun areContentsTheSame(oldPosition: Int, newPosition: Int) = false
}

class DiffCallback : DiffUtil.ItemCallback<DifferentiableItem>() {
  
  override fun areItemsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem): Boolean {
    return oldItem.id == newItem.id
  }
  
  override fun areContentsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem): Boolean {
    return oldItem == newItem
  }
}