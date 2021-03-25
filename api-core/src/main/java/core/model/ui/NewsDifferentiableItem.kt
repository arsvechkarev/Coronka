package core.model.ui

import api.recycler.DifferentiableItem

/**
 * News item to be displayed in a list
 */
data class NewsDifferentiableItem(
  override val id: String,
  val title: String,
  val description: String,
  val webUrl: String,
  val formattedDate: String,
  val imageUrl: String
) : DifferentiableItem