package com.arsvechkarev.rankings.presentation

import base.views.Chip

class ChipGroup(private vararg val chips: Chip) {
  
  private var currentChip = chips[0]
  
  var onNewChipSelected: (Chip) -> Unit = {}
  
  init {
    chips.forEach { chip ->
      chip.setOnClickListener {
        val chipInner = it as Chip
        if (chipInner != currentChip) {
          currentChip.isSelected = false
          currentChip = chipInner
          currentChip.isSelected = true
          onNewChipSelected(currentChip)
        }
      }
    }
  }
  
  fun setSelectedChipById(chipId: Int) {
    val chip = chips.find { it.id == chipId }!!
    currentChip = chip
    currentChip.isSelected = true
  }
}