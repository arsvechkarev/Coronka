package core.model

import core.recycler.DisplayableItem

object FAQHeader : DisplayableItem {
  override val id = 0
  override fun equals(other: Any?) = other is FAQHeader
}