package com.arsvechkarev.views.behaviors

import android.view.View
import androidx.core.view.ViewCompat

class ViewOffsetHelper(val view: View) {
  
  var topAndBottomOffset = 0
    private set
  private var layoutTop: Int = 0
  
  fun onViewLayout() {
    layoutTop = view.top
  }
  
  fun setTopAndBottomOffset(offset: Int) {
    if (topAndBottomOffset != offset) {
      topAndBottomOffset = offset
      ViewCompat.offsetTopAndBottom(view, topAndBottomOffset - (view.top - layoutTop))
    }
  }
}