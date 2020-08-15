package com.arsvechkarev.rankings.domain

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout

class ReverseDrawingOrderCoordinatorLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : CoordinatorLayout(context, attrs) {
  
  init {
    isChildrenDrawingOrderEnabled = true
  }
  
  override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
    return childCount - drawingPosition - 1
  }
}