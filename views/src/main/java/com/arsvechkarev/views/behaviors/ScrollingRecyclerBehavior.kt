package com.arsvechkarev.views.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import androidx.coordinatorlayout.widget.CoordinatorLayout
import core.extenstions.getBehavior

class ScrollingRecyclerBehavior<V : View>(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<V>() {
  
  private var minHeaderHeight = -1
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    return dependency is Header
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    minHeaderHeight = dependency.getBehavior<HeaderBehavior<*>>().minHeight
    child.top = dependency.bottom
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
    val top = findHeader(parent).bottom
    child.layout(0, top, parent.width, top + child.measuredHeight)
    return true
  }
  
  private fun findHeader(parent: CoordinatorLayout): View {
    repeat(parent.childCount) {
      val child = parent.getChildAt(it)
      if (child is Header) {
        return child
      }
    }
    throw IllegalStateException()
  }
} 