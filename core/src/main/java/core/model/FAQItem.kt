package core.model

import core.recycler.DisplayableItem

data class FAQItem(
  val title: String,
  val description: String
) : DisplayableItem {
  override val id = title.hashCode()
}