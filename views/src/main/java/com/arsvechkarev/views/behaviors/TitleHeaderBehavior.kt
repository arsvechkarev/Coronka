package com.arsvechkarev.views.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arsvechkarev.viewdsl.forEachChild
import com.arsvechkarev.viewdsl.hasBehavior
import core.extenstions.f

class TitleHeaderBehavior(
  private val isViewTheDependency: (View) -> Boolean
) : CoordinatorLayout.Behavior<View>() {
  
  private var scrollRange = 0
  private var dependentViewMaxTop = 0
  private var dependentViewMinTop = 0
  private var initialChildTop = 0
  private var childHeight = 0
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
    val isTheDependency = isViewTheDependency(dependency)
    if (isTheDependency) {
      val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
      if (scrollRange == 0) {
        scrollRange = (behavior as ScrollableContentBehavior).computeScrollRange()
        dependentViewMaxTop = child.bottom
        dependentViewMinTop = dependentViewMaxTop - scrollRange
        initialChildTop = child.top
        childHeight = child.height
      }
    }
    return isTheDependency
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
    child.top = getTopForHeader(parent, dependency)
    return true
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
    parent.onLayoutChild(child, layoutDirection)
    child.top = getTopForHeader(parent)
    return true
  }
  
  private fun getTopForHeader(parent: CoordinatorLayout, dependency: View? = null): Int {
    val view = dependency ?: findViewWithStatsContentBehavior(parent)
    val normalizedTop = view.top - dependentViewMinTop
    val percent = normalizedTop.f / (dependentViewMaxTop - dependentViewMinTop)
    return (initialChildTop - (1f - percent) * childHeight).toInt()
  }
  
  private fun findViewWithStatsContentBehavior(parent: CoordinatorLayout): View {
    var header: View? = null
    parent.forEachChild { child ->
      if (child.hasBehavior<ScrollableContentBehavior<*>>()) {
        header = child
      }
    }
    return header!!
  }
}