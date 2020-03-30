package core.model

import core.recycler.DisplayableItem

data class GeneralInfo(
  val confirmed: Int,
  val recovered: Int,
  val deaths: Int
) : DisplayableItem {
  // id is not important, because this class will not be used more than once in recycler
  override val id = "id"
  override val type = TYPE_GENERAL_INFO
}