package core.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Item for displaying in [RecyclerView]
 */
interface SortableDisplayableItem : DisplayableItem {
  
  /**
   * Id to distinguish two different elements
   */
  val id: Int
  
  /**
   * Every class inherits from [SortableDisplayableItem] should override equals in order to compare elements
   * properly
   */
  override fun equals(other: Any?): Boolean
  
  /**
   * Callback for updating items in recycler view
   */
  open class DiffCallBack<T : SortableDisplayableItem> :
    DiffUtil.ItemCallback<T>() {
  
    override fun areItemsTheSame(oldItem: T, newItem: T) =
        oldItem.id == newItem.id
  
    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem == newItem
  }
}