package com.arsvechkarev.views.behaviors

import android.view.View
import androidx.core.view.ViewCompat

class ViewOffsetHelper(val view: View, private val slideRangeCoefficient: Float) {
  
  private var layoutTop: Int = 0
  
  var topAndBottomOffset = 0
    private set
  
  val maxScrollingRange: Int
    get() = (-view.height * (1 - slideRangeCoefficient)).toInt()
  
  fun updateOffset(dy: Int): Int {
    val prefOffset = topAndBottomOffset
    if (dy != 0) {
      val resultOffset = (topAndBottomOffset - dy).coerceIn(maxScrollingRange, 0)
      topAndBottomOffset = resultOffset
      ViewCompat.offsetTopAndBottom(view, topAndBottomOffset - (view.top - layoutTop))
    }
    return prefOffset - topAndBottomOffset
  }
  
}