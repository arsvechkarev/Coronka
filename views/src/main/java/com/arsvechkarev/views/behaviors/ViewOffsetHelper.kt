package com.arsvechkarev.views.behaviors

import android.view.View
import androidx.core.view.ViewCompat

class ViewOffsetHelper(val view: View, var slideRangeCoefficient: Float) {
  
  var topAndBottomOffset = 0
    private set
  
  val maxScrollingRange: Int
    get() = (-view.height * slideRangeCoefficient).toInt()
  
  fun updateOffset(dy: Int): Int {
    val prefOffset = topAndBottomOffset
    if (dy != 0) {
      val resultOffset = (topAndBottomOffset - dy).coerceIn(maxScrollingRange, 0)
      topAndBottomOffset = resultOffset
      ViewCompat.offsetTopAndBottom(view, topAndBottomOffset - view.top)
    }
    return prefOffset - topAndBottomOffset
  }
  
}