package core.recycler

/**
 * Item for [androidx.recyclerview.widget.RecyclerView] that can be compared to other items
 */
interface DifferentiableItem : DisplayableItem {
  
  /**
   * Id to distinguish two different elements
   */
  val id: String
  
  /**
   * Every class inherits from [DifferentiableItem] should override equals in order to compare elements
   * properly
   */
  override fun equals(other: Any?): Boolean
}