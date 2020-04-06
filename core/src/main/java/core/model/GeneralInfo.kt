package core.model

import core.recycler.DisplayableItem

data class GeneralInfo(
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int
)

data class DisplayableGeneralInfo(
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val optionType: OptionType
) : DisplayableItem {
  // id is not important, because this class will not be used more than once in recycler
  override val id = -1
  override val type = TYPE_GENERAL_INFO
}