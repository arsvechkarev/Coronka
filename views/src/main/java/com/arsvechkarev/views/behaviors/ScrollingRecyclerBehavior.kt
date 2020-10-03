package com.arsvechkarev.views.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import androidx.coordinatorlayout.widget.CoordinatorLayout
import core.extenstions.getBehavior
import core.extenstions.getBehaviorSafe

class ScrollingRecyclerBehavior<V : View>(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<V>() {
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    return dependency.getBehaviorSafe<HeaderBehavior<*>>() != null
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
    val findHeader = findHeader(parent)
    val height = parent.height - findHeader.getBehavior<HeaderBehavior<*>>().minHeight
    val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightSpec,
      heightUsed)
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
      if (child.getBehaviorSafe<HeaderBehavior<*>>() != null) {
        return child
      }
    }
    throw IllegalStateException()
  }
} 