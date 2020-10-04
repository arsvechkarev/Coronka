package com.arsvechkarev.views.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import core.extenstions.hasBehavior

class ViewUnderHeaderBehavior<V : View>(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<V>() {
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    return dependency.hasBehavior<HeaderBehavior<*>>()
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
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
    val header = findHeader(parent)
    val height = parent.measuredHeight - header.measuredHeight
    val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
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
      if (child.hasBehavior<HeaderBehavior<*>>()) {
        return child
      }
    }
    throw IllegalStateException()
  }
}