package com.arsvechkarev.stats.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.arsvechkarev.stats.R
import core.extenstions.f

class TitleHeaderBehavior<V : View>(
  context: Context,
  attrs: AttributeSet
) : CoordinatorLayout.Behavior<V>() {
  
  private val offset = (5 * context.resources.displayMetrics.density).toInt()
  private var scrollRange = 0
  private var dependentViewMaxTop = 0
  private var dependentViewMinTop = 0
  private var initialChildTop = 0
  private var childHeight = 0
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    val isTheDependency = dependency.id == R.id.scrollingContentView
    if (isTheDependency) {
      val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
      if (scrollRange == 0) {
        scrollRange = (behavior as StatsContentBehavior).computeScrollRange()
        dependentViewMaxTop = child.bottom - offset
        dependentViewMinTop = dependentViewMaxTop - scrollRange
        initialChildTop = child.top
        childHeight = child.height
      }
    }
    return isTheDependency
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
    if (dependentViewMinTop == dependentViewMaxTop) {
      // Hasn't properly initialized yet
      return true
    }
    val normalizedTop = dependency.top - dependentViewMinTop
    val percent = normalizedTop.f / (dependentViewMaxTop - dependentViewMinTop)
    child.top = (initialChildTop - (1 - percent) * childHeight).toInt()
    return true
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
    parent.onLayoutChild(child, layoutDirection)
    ViewCompat.offsetTopAndBottom(child, offset)
    return true
  }
}