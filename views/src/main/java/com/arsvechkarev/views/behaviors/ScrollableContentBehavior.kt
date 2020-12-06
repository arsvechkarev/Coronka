package com.arsvechkarev.views.behaviors

import android.content.Context
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ScrollingView
import androidx.core.view.ViewCompat
import com.arsvechkarev.views.DrawerLayout
import core.INVALID_POINTER
import kotlin.math.abs
import kotlin.math.roundToInt

class ScrollableContentBehavior<V>(context: Context) :
  CoordinatorLayout.Behavior<V>() where V : View, V : ScrollingView {
  
  private val scroller = OverScroller(context)
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var statsContentViewOffsetHelper: StatsContentViewOffsetHelper<V>? = null
  private var velocityTracker: VelocityTracker? = null
  private var flingRunnable: Runnable? = null
  private var isBeingDragged = false
  private var activePointerId = INVALID_POINTER
  private var lastMotionY = 0
  
  var respondToTouches = true
  
  fun computeScrollRange(): Int {
    return statsContentViewOffsetHelper?.getScrollRange() ?: 0
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
    parent.onLayoutChild(child, layoutDirection)
    statsContentViewOffsetHelper = StatsContentViewOffsetHelper(child, parent.height)
    return true
  }
  
  override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    if (!respondToTouches) return false
    val action = event.action
    if (action == ACTION_MOVE && isBeingDragged) {
      return true
    }
    when (event.actionMasked) {
      ACTION_DOWN -> {
        stopScroller(child)
        isBeingDragged = false
        activePointerId = event.getPointerId(0)
        lastMotionY = event.y.toInt()
        ensureVelocityTracker()
      }
      ACTION_POINTER_DOWN -> {
        activePointerId = event.getPointerId(event.actionIndex)
        lastMotionY = event.getY(event.actionIndex).toInt()
      }
      ACTION_MOVE -> {
        stopScroller(child)
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
          findDrawerParent(parent).respondToTouches = false
          isBeingDragged = true
          lastMotionY = y
        }
      }
      ACTION_POINTER_UP -> {
        onPointerUp(event)
      }
      ACTION_UP, ACTION_CANCEL -> {
        findDrawerParent(parent).respondToTouches = true
        endTouch()
      }
    }
    velocityTracker?.addMovement(event)
    return isBeingDragged
  }
  
  override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    if (!respondToTouches) return false
    when (event.actionMasked) {
      ACTION_DOWN -> {
        stopScroller(child)
        val y = event.y.toInt()
        lastMotionY = y
        activePointerId = event.getPointerId(0)
        ensureVelocityTracker()
      }
      ACTION_POINTER_DOWN -> {
        activePointerId = event.getPointerId(event.actionIndex)
        lastMotionY = event.getY(event.actionIndex).toInt()
      }
      ACTION_MOVE -> {
        val activePointerIndex = event.findPointerIndex(activePointerId)
        if (activePointerIndex == -1) {
          return true
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
          updateOffset(dy)
        }
      }
      ACTION_POINTER_UP -> {
        onPointerUp(event)
      }
      ACTION_UP -> {
        findDrawerParent(parent).respondToTouches = true
        velocityTracker?.addMovement(event)
        velocityTracker?.computeCurrentVelocity(1000)
        val velocityY = velocityTracker?.getYVelocity(activePointerId) ?: return true
        fling(child, velocityY)
        endTouch()
      }
      ACTION_CANCEL -> {
        findDrawerParent(parent).respondToTouches = true
        endTouch()
      }
    }
    velocityTracker?.addMovement(event)
    return true
  }
  
  private fun onPointerUp(event: MotionEvent) {
    val actionIndex = event.actionIndex
    if (event.getPointerId(actionIndex) == activePointerId) {
      val newIndex = if (actionIndex == 0) 1 else 0
      activePointerId = event.getPointerId(newIndex)
      lastMotionY = event.getY(newIndex).toInt()
    }
  }
  
  private fun findDrawerParent(view: View): DrawerLayout {
    var parent = view.parent
    while (parent != null) {
      if (parent is DrawerLayout) {
        return parent
      }
      parent = parent.parent
    }
    throw IllegalStateException()
  }
  
  private fun stopScroller(child: V) {
    child.removeCallbacks(flingRunnable)
    scroller.abortAnimation()
    flingRunnable = null
  }
  
  private fun updateOffset(dy: Int): Int {
    return statsContentViewOffsetHelper!!.updateOffset(dy)
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
    velocityTracker?.recycle()
    velocityTracker = null
  }
  
  private inner class FlingRunnable(val child: V) : Runnable {
    
    private var latestY: Int = Int.MAX_VALUE
    
    override fun run() {
      if (scroller.computeScrollOffset()) {
        if (latestY != Int.MAX_VALUE) {
          updateOffset(scroller.currY - latestY)
        }
        latestY = scroller.currY
        child.postOnAnimation(this)
      }
    }
  }
}