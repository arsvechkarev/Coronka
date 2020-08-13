package com.arsvechkarev.stats.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import core.INVALID_POINTER
import kotlin.math.abs
import kotlin.math.roundToInt

class StatsContentBehavior<V>(context: Context, attrs: AttributeSet?) :
  CoordinatorLayout.Behavior<V>() where V : View, V : ScrollingView {
  
  private val scroller = OverScroller(context)
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var statsContentViewOffsetHelper: StatsContentViewOffsetHelper<V>? = null
  private var velocityTracker: VelocityTracker? = null
  private var flingRunnable: Runnable? = null
  
  private var isBeingDragged = false
  private var activePointerId = INVALID_POINTER
  private var lastMotionY = 0
  
  fun computeScrollRange(): Int {
    return statsContentViewOffsetHelper?.getScrollRange() ?: 0
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
    parent.onLayoutChild(child, layoutDirection)
    if (statsContentViewOffsetHelper == null) {
      statsContentViewOffsetHelper = StatsContentViewOffsetHelper(child)
    }
    statsContentViewOffsetHelper!!.onViewLayout(parent.height)
    return true
  }
  
  override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    val action = event.action
    
    if (action == MotionEvent.ACTION_MOVE && isBeingDragged) {
      return true
    }
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        stopScroller(child)
        isBeingDragged = false
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (parent.isPointInChildBounds(child, x, y)) {
          lastMotionY = y
          activePointerId = event.getPointerId(0)
          ensureVelocityTracker()
        }
      }
      MotionEvent.ACTION_MOVE -> {
        stopScroller(child)
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
      MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        endTouch()
      }
    }
    if (velocityTracker != null) {
      velocityTracker!!.addMovement(event)
    }
    return isBeingDragged
  }
  
  override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        stopScroller(child)
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
      MotionEvent.ACTION_MOVE -> {
        val activePointerIndex = event.findPointerIndex(activePointerId)
        if (activePointerIndex == -1) {
          return false
        }
        val y = event.getY(activePointerIndex).toInt()
        var dy = y - lastMotionY
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
          // We're being dragged so scroll the view
          updateDyOffset(dy)
        }
      }
      MotionEvent.ACTION_UP -> {
        if (velocityTracker != null) {
          velocityTracker!!.addMovement(event)
          velocityTracker!!.computeCurrentVelocity(1000)
          val velocityY = velocityTracker!!.getYVelocity(activePointerId)
          fling(child, velocityY)
        }
        endTouch()
      }
      MotionEvent.ACTION_CANCEL -> {
        endTouch()
      }
    }
    if (velocityTracker != null) {
      velocityTracker!!.addMovement(event)
    }
    return true
  }
  
  private fun stopScroller(child: V) {
    child.removeCallbacks(flingRunnable)
    scroller.abortAnimation()
    flingRunnable = null
  }
  
  private fun updateDyOffset(dy: Int): Int {
    return statsContentViewOffsetHelper!!.updateDyOffset(dy)
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
      0,
      0,
      velocityY.roundToInt(),
      0,
      0,
      statsContentViewOffsetHelper!!.minScrollerY,
      statsContentViewOffsetHelper!!.maxScrollerY
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
    if (velocityTracker != null) {
      velocityTracker!!.recycle()
      velocityTracker = null
    }
  }
  
  private inner class FlingRunnable(val child: V) : Runnable {
    
    private var latestY: Int = Int.MAX_VALUE
    
    override fun run() {
      if (scroller.computeScrollOffset()) {
        if (latestY != Int.MAX_VALUE) {
          updateDyOffset(scroller.currY - latestY)
        }
        latestY = scroller.currY
        child.postOnAnimation(this)
      }
    }
  }
}