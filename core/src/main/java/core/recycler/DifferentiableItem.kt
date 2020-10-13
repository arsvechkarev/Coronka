package core.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Item for displaying in [RecyclerView]
 */
interface DifferentiableItem : DisplayableItem {
  
  /**
   * Id to distinguish two different elements
   */
  val id: Int
  
  /**
   * Every class inherits from [DifferentiableItem] should override equals in order to compare elements
   * properly
   */
  override fun equals(other: Any?): Boolean
  
  /**
   * Callback for updating items in recycler view
   */
  class DiffCallBack : DiffUtil.ItemCallback<DifferentiableItem>() {
    
    override fun areItemsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem) =
        oldItem.id == newItem.id
    
    override fun areContentsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem) =
        oldItem == newItem
  }
  
  /**
   * Callback for updating items in recycler view when items are always considered to be
   * not equal
   */
  object AlwaysFalseCallback : DiffUtil.ItemCallback<DifferentiableItem>() {
    
    override fun areItemsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem) = false
    
    override fun areContentsTheSame(oldItem: DifferentiableItem, newItem: DifferentiableItem) = false
  }
}