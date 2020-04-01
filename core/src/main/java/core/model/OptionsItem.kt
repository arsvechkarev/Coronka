package core.model

import core.recycler.DisplayableItem

object OptionsItem : DisplayableItem {
  override val type = TYPE_OPTIONS
  override val id = -1
  override fun equals(other: Any?) = true
}