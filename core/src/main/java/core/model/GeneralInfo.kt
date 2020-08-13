package core.model

import core.recycler.DisplayableItem

data class GeneralInfo(
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int
)

class DisplayableGeneralInfo(
  val confirmed: Int,
  val deaths: Int,
  val recovered: Int,
  val optionType: OptionType
) : DisplayableItem