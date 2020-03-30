package core.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Item for displaying in [RecyclerView]
 */
interface DisplayableItem {
  
  /**
   * Type to bind recycler adapter
   */
  val type: Int
  
  /**
   * Id to distinguish two different elements
   */
  val id: String
  
  /**
   * Every class inherits from [DisplayableItem] should override equals in order to compare elements
   * properly
   */
  override operator fun equals(other: Any?): Boolean
  
  /**
   * Callback for updating items in recycler view
   */
  open class DiffCallBack<T : DisplayableItem> : DiffUtil.ItemCallback<T>() {
    
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.id == newItem.id
    
    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem == newItem
  }
}