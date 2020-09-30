package com.arsvechkarev.rankings.presentation

import com.arsvechkarev.views.Chip

class ChipGroup(vararg chips: Chip) {
  
  private var currentChip = chips[0]
  
  var onNewChipSelected: (Chip) -> Unit = {}
  
  init {
    chips.forEach { chip ->
      chip.setOnClickListener {
        val chipInner = it as Chip
        if (chipInner != currentChip) {
          currentChip.isActive = false
          currentChip = chipInner
          currentChip.isActive = true
          onNewChipSelected(currentChip)
        }
      }
    }
  }
}