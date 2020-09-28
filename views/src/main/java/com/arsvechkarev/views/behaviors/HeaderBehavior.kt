package com.arsvechkarev.views.behaviors

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
import core.extenstions.assertThat
import core.extenstions.f
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Behavior for header view in coordinator layout
 */
class HeaderBehavior<V : View>(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<V>() {
  
  private val scroller = OverScroller(context)
  private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var viewOffsetHelper: ViewOffsetHelper? = null
  private var velocityTracker: VelocityTracker? = null
  private var flingRunnable: Runnable? = null
  
  private var isBeingDragged = false
  private var activePointerId = INVALID_POINTER
  private var lastMotionY = 0
  
  private val offsetListeners = ArrayList<((fraction: Float) -> Unit)>()
  
  private var currentOffset = 0f
  
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
   * Example: if header height is 400 and slideRangeCoefficient is 0.8, total range would be 400 * 0.8 = 320
   */
  var slideRangeCoefficient = 1f
    set(value) {
      assertThat(value in 0f..1f) { "Range should be in range 0..1" }
      field = value
    }
  
  /**
   * Determines whether the header should react to touch events
   */
  var reactToTouches: Boolean = true
  
  /**
   * Adds an offset listener
   *
   * @param onOffsetListener lambda that receives fraction from 0 to 1 of current offset to total
   * scrolling range
   */
  fun addOnOffsetListener(onOffsetListener: (fraction: Float) -> Unit) {
    offsetListeners.add(onOffsetListener)
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout,
                             child: V, layoutDirection: Int): Boolean {
    val prevOffset = viewOffsetHelper?.topAndBottomOffset ?: 0
    viewOffsetHelper = ViewOffsetHelper(child, slideRangeCoefficient)
    parent.onLayoutChild(child, layoutDirection)
    ViewCompat.offsetTopAndBottom(child, prevOffset)
    offsetListeners.forEach { it(currentOffset) }
    return true
  }
  
  override fun onInterceptTouchEvent(parent: CoordinatorLayout,
                                     child: V, event: MotionEvent): Boolean {
    if (!reactToTouches) return false
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
    if (!reactToTouches) return false
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
    return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
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
    val targetViewOffset = (target as? ScrollingView)?.computeVerticalScrollOffset() ?: 0
    if (targetViewOffset == 0) {
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
    if (dyUnconsumed < 0) {
      consumed[1] = updateTopBottomOffset(dyUnconsumed)
    }
  }
  
  private fun updateTopBottomOffset(dy: Int): Int {
    notifyOffsetListeners()
    return viewOffsetHelper!!.updateOffset(dy)
  }
  
  private fun notifyOffsetListeners() {
    currentOffset = abs(
      viewOffsetHelper!!.topAndBottomOffset.f / viewOffsetHelper!!.maxScrollingRange)
    offsetListeners.forEach { it(currentOffset) }
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
      viewOffsetHelper!!.maxScrollingRange.toInt(),
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