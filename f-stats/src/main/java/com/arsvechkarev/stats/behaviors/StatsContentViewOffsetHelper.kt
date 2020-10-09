package com.arsvechkarev.stats.behaviors

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ScrollingView

class StatsContentViewOffsetHelper<V>(val view: V) where V : View, V : ScrollingView {
  
  private var maxTop = 0
  private var parentHeight = 0
  private var layoutTop: Int = 0
  private var layoutBottom: Int = 0
  
  var minScrollerY = 0
    private set
  
  var maxScrollerY = 0
    private set
  
  fun onViewLayout(parentHeight: Int) {
    this.parentHeight = parentHeight
    layoutTop = view.top
    layoutBottom = view.bottom
    val viewScrollRange = view.computeVerticalScrollRange()
    val topMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    maxTop = layoutTop - (topMargin + viewScrollRange - parentHeight)
    minScrollerY = maxTop
    maxScrollerY = topMargin + viewScrollRange
  }
  
  fun updateOffset(offset: Int): Int {
    val newTop = view.top + offset
    val oldTop = view.top
    view.top = newTop.coerceIn(maxTop, layoutTop)
    return view.top - oldTop
  }
  
  fun getScrollRange(): Int {
    val viewScrollRange = view.computeVerticalScrollRange()
    val topMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    return topMargin + viewScrollRange - parentHeight
  }
}