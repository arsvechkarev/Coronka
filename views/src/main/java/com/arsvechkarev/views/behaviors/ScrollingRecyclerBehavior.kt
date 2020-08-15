package com.arsvechkarev.views.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import core.extenstions.getBehavior

class ScrollingRecyclerBehavior<V : View>(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<V>() {
  
  private var minHeaderHeight = -1
  private var offset = -1
  private var offsettingFirstTime = true
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    return dependency is Header
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    minHeaderHeight = dependency.getBehavior<HeaderBehavior<*>>().minHeight
    val topMargin = (child.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    if (offsettingFirstTime) {
      offset = dependency.bottom + topMargin
    } else {
      offset = dependency.bottom - child.top + topMargin
    }
    offsettingFirstTime = false
    ViewCompat.offsetTopAndBottom(child, offset)
    return true
  }
  
  override fun onMeasureChild(
    parent: CoordinatorLayout,
    child: V,
    parentWidthMeasureSpec: Int,
    widthUsed: Int,
    parentHeightMeasureSpec: Int,
    heightUsed: Int
  ): Boolean {
    val height = parent.height - minHeaderHeight
    val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightSpec, heightUsed)
    return true
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
    parent.onLayoutChild(child, layoutDirection)
    val topMargin = (child.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    ViewCompat.offsetTopAndBottom(child, offset - topMargin)
    return true
  }
}