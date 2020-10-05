package com.arsvechkarev.views.behaviors

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import core.extenstions.AccelerateDecelerateInterpolator
import core.extenstions.DURATION_SHORT
import core.extenstions.assertThat
import core.extenstions.doOnEnd
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Behavior for header view in coordinator layout
 */
class HeaderBehavior<V : View>(context: Context, attrs: AttributeSet) :
  CoordinatorLayout.Behavior<V>() {
  
  private val scroller = OverScroller(context)
  private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var viewOffsetHelper: ViewOffsetHelper? = null
  private var velocityTracker: VelocityTracker? = null
  private var flingRunnable: Runnable? = null
  private var isBeingDragged = false
  
  private var activePointerId = INVALID_POINTER
  private var lastMotionY = 0
  private var offsetFromPreviousLayout = 0
  
  private val scrollAnimator = ValueAnimator().apply {
    duration = DURATION_SHORT
    interpolator = AccelerateDecelerateInterpolator
    addUpdateListener {
      val offset = it.animatedValue as Int
      updateTopBottomOffset(viewOffsetHelper!!.topAndBottomOffset - offset)
    }
  }
  
  /**
   * Returns minimum height the header can have taking into account [slideRangeCoefficient]
   */
  val minHeight: Int
    get() {
      return ((viewOffsetHelper?.view?.height ?: 0) * slideRangeCoefficient).toInt()
    }
  
  /**
   * Determines how much slide range should be squashed compared to header height
   *
   * Example: if header height is 400 and slideRangeCoefficient is 0.8, total range
   * would be 400 * 0.8 = 320
   */
  var slideRangeCoefficient = 1f
    set(value) {
      assertThat(value in 0f..1f) { "Range should be in range 0..1" }
      field = value
    }
  
  /**
   * Determines whether the header should respond to touch events
   */
  var isScrollable: Boolean = true
  
  /**
   * Determines whether the header should respond to touch events received from header view
   */
  var respondToHeaderTouches: Boolean = false
  
  /**
   * Smoothly scrolls header view to initial position
   */
  fun animateScrollToTop(andThen: () -> Unit = {}) {
    if (offsetFromPreviousLayout != 0) {
      scrollAnimator.setIntValues(offsetFromPreviousLayout, 0)
      scrollAnimator.start()
      scrollAnimator.doOnEnd(andThen)
    } else {
      andThen()
    }
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout,
                             child: V, layoutDirection: Int): Boolean {
    offsetFromPreviousLayout = viewOffsetHelper?.topAndBottomOffset ?: 0
    viewOffsetHelper = ViewOffsetHelper(child, slideRangeCoefficient)
    parent.onLayoutChild(child, layoutDirection)
    ViewCompat.offsetTopAndBottom(child, offsetFromPreviousLayout)
    return true
  }
  
  override fun onInterceptTouchEvent(parent: CoordinatorLayout,
                                     child: V, event: MotionEvent): Boolean {
    if (!allowScrolling || !respondToHeaderTouches) return false
    if (event.action == ACTION_MOVE && isBeingDragged) {
      return true
    }
    when (event.actionMasked) {
      ACTION_DOWN -> {
        isBeingDragged = false
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (parent.isPointInChildBounds(child, x, y)) {
          lastMotionY = y
          activePointerId = event.getPointerId(0)
          ensureVelocityTracker()
        }
      }
      ACTION_MOVE -> {
        val activePointerId = activePointerId
        if (activePointerId == INVALID_POINTER) {
          // If we don't have a valid id, the touch down wasn't on content.
          return false
        }
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex == -1) {
          return false
        }
        val y = event.getY(pointerIndex).toInt()
        val yDiff = abs(y - lastMotionY)
        if (yDiff > touchSlop) {
          isBeingDragged = true
          lastMotionY = y
        }
      }
      ACTION_CANCEL, ACTION_UP -> {
        endTouch()
      }
    }
    velocityTracker?.addMovement(event)
    return isBeingDragged
  }
  
  override fun onTouchEvent(parent: CoordinatorLayout,
                            child: V, event: MotionEvent): Boolean {
    if (!allowScrolling || !respondToHeaderTouches) return false
    when (event.actionMasked) {
      ACTION_DOWN -> {
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (parent.isPointInChildBounds(child, x, y)) {
          lastMotionY = y
          activePointerId = event.getPointerId(0)
          ensureVelocityTracker()
        } else {
          return false
        }
      }
      ACTION_MOVE -> {
        val activePointerIndex = event.findPointerIndex(activePointerId)
        if (activePointerIndex == -1) {
          return false
        }
        val y = event.getY(activePointerIndex).toInt()
        var dy = lastMotionY - y
        if (!isBeingDragged && abs(dy) > touchSlop) {
          isBeingDragged = true
          if (dy > 0) {
            dy -= touchSlop
          } else {
            dy += touchSlop
          }
        }
        if (isBeingDragged) {
          lastMotionY = y
          updateTopBottomOffset(dy)
        }
      }
      ACTION_UP -> {
        if (velocityTracker != null) {
          velocityTracker!!.addMovement(event)
          velocityTracker!!.computeCurrentVelocity(1000)
          val velocityY = velocityTracker!!.getYVelocity(activePointerId)
          fling(child, velocityY)
        }
        endTouch()
      }
      ACTION_CANCEL -> {
        endTouch()
      }
    }
    velocityTracker?.addMovement(event)
    return true
  }
  
  override fun onStartNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: V,
    directTargetChild: View,
    target: View,
    axes: Int,
    type: Int
  ): Boolean {
    return allowScrolling && (axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
  }
  
  
  override fun onNestedPreScroll(
    coordinatorLayout: CoordinatorLayout,
    child: V,
    target: View,
    dx: Int,
    dy: Int,
    consumed: IntArray,
    type: Int
  ) {
    val targetViewOffset = (target as? ScrollingView)?.computeVerticalScrollOffset()
    if (allowScrolling && targetViewOffset == 0) {
      consumed[1] = updateTopBottomOffset(dy)
    }
  }
  
  override fun onNestedScroll(
    coordinatorLayout: CoordinatorLayout,
    child: V,
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) {
    if (allowScrolling && dyUnconsumed < 0) {
      consumed[1] = updateTopBottomOffset(dyUnconsumed)
    }
  }
  
  private fun updateTopBottomOffset(dy: Int): Int {
    return viewOffsetHelper!!.updateOffset(dy)
  }
  
  private fun fling(
    child: V,
    velocityY: Float) {
    if (flingRunnable != null) {
      child.removeCallbacks(flingRunnable)
      flingRunnable = null
    }
    scroller.fling(
      0,
      viewOffsetHelper!!.topAndBottomOffset,
      0,
      velocityY.roundToInt(),
      0,
      0,
      viewOffsetHelper!!.maxScrollingRange,
      0
    )
    if (scroller.computeScrollOffset()) {
      flingRunnable = FlingRunnable(child)
      ViewCompat.postOnAnimation(child, flingRunnable)
    }
  }
  
  private fun ensureVelocityTracker() {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain()
    }
  }
  
  private fun endTouch() {
    isBeingDragged = false
    activePointerId = INVALID_POINTER
    velocityTracker?.recycle()
    velocityTracker = null
  }
  
  private val allowScrolling get() = !scrollAnimator.isRunning && isScrollable
  
  private inner class FlingRunnable(val child: V) : Runnable {
    
    override fun run() {
      if (scroller.computeScrollOffset()) {
        updateTopBottomOffset(viewOffsetHelper!!.topAndBottomOffset - scroller.currY)
        child.postOnAnimation(this)
      }
    }
  }
  
  private companion object {
    private const val INVALID_POINTER = -1
  }
}