package base.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class HidingButtonBehavior(context: Context? = null, attrs: AttributeSet? = null) :
  CoordinatorLayout.Behavior<View>() {
  
  private var scrolled = 0
  private var isAnimating = false
  
  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: View,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean {
    return axes and View.SCROLL_AXIS_VERTICAL != 0
  }
  
  override fun onNestedPreScroll(
    coordinatorLayout: CoordinatorLayout,
    child: View,
    target: View,
    dx: Int,
    dy: Int,
    consumed: IntArray,
    type: Int
  ) {
    scrolled += dy
    val isScrollingDown = dy > 0
    animateIfNeeded(child, isScrollingDown)
  }
  
  override fun onStopNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: View,
    target: View,
    type: Int
  ) {
    scrolled = 0
  }
  
  private fun animateIfNeeded(child: View, isScrollingDown: Boolean) {
    if (isAnimating) return
    val range = getRange(child)
    if (isScrollingDown) {
      if (child.translationY <= 0f && scrolled >= range) {
        performAnimation(child, range)
      }
    } else {
      scrolled = 0
      if (child.translationY > 0f) {
        performAnimation(child, -range)
      }
    }
  }
  
  private fun performAnimation(child: View, translation: Float) {
    scrolled = 0
    isAnimating = true
    child.animate()
        .translationYBy(translation)
        .withEndAction { isAnimating = false }
        .start()
  }
  
  private fun getRange(child: View): Float {
    return child.height * 1.5f
  }
}