package com.arsvechkarev.common

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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arsvechkarev.common.BottomSheetBehavior.State.HIDDEN
import com.arsvechkarev.common.BottomSheetBehavior.State.SHOWN
import core.INVALID_POINTER
import core.extenstions.DURATION_DEFAULT
import kotlin.math.abs

class BottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<V>() {
  
  enum class State {
    SHOWN, HIDDEN
  }
  
  private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private var maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  private var activePointerId = INVALID_POINTER
  private var isBeingDragged = false
  private var latestY = -1
  private var velocityTracker: VelocityTracker? = null
  
  private var bottomSheetOffsetHelper: BottomSheetOffsetHelper? = null
  private var currentState = HIDDEN
  private var bottomSheet: View? = null
  private var parentHeight = 0
  private var slideRange = 0
  
  private val slideAnimator = ValueAnimator().apply {
    addUpdateListener {
      val value = it.animatedValue as Int
      bottomSheetOffsetHelper!!.updateTop(value)
    }
  }
  
  fun show() {
    if (currentState == SHOWN || slideAnimator.isRunning) return
    bottomSheet!!.post {
      currentState = SHOWN
      slideAnimator.duration = DURATION_DEFAULT
      slideAnimator.setIntValues(bottomSheet!!.top, parentHeight - slideRange)
      slideAnimator.start()
    }
  }
  
  fun hide() {
    if (currentState == HIDDEN || slideAnimator.isRunning) return
    bottomSheet!!.post {
      currentState = HIDDEN
      slideAnimator.duration = DURATION_DEFAULT
      slideAnimator.setIntValues(bottomSheet!!.top, parentHeight)
      slideAnimator.start()
    }
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
    child.layout(0, parent.height - child.measuredHeight, parent.width, parent.height)
    bottomSheet = child
    slideRange = child.height
    parentHeight = parent.height
    if (bottomSheetOffsetHelper == null) {
      bottomSheetOffsetHelper = BottomSheetOffsetHelper(child)
    }
    bottomSheetOffsetHelper!!.onViewLayout(parentHeight)
    if (currentState == HIDDEN) {
      bottomSheet!!.top = parentHeight
    }
    return true
  }
  
  override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    if (slideAnimator.isRunning) return false
    val action = event.action
    if (action == ACTION_MOVE && isBeingDragged) {
      return true
    }
    when (event.actionMasked) {
      ACTION_DOWN -> {
        isBeingDragged = false
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (parent.isPointInChildBounds(child, x, y)) {
          latestY = y
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
        val yDiff = abs(y - latestY)
        if (yDiff > touchSlop) {
          isBeingDragged = true
          latestY = y
        }
      }
      ACTION_CANCEL, ACTION_UP -> {
        endTouch()
      }
    }
    if (velocityTracker != null) {
      velocityTracker!!.addMovement(event)
    }
    return isBeingDragged
  }
  
  override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
    if (slideAnimator.isRunning) return false
    when (event.actionMasked) {
      ACTION_DOWN -> {
        val x = event.x.toInt()
        val y = event.y.toInt()
        if (parent.isPointInChildBounds(child, x, y)) {
          latestY = y
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
        var dy = y - latestY
        if (!isBeingDragged && abs(dy) > touchSlop) {
          isBeingDragged = true
          if (dy > 0) {
            dy -= touchSlop
          } else {
            dy += touchSlop
          }
        }
        if (isBeingDragged) {
          latestY = y
          // We're being dragged so scroll the view
          updateDyOffset(dy)
        }
      }
      ACTION_UP -> {
        velocityTracker!!.addMovement(event)
        velocityTracker!!.computeCurrentVelocity(1000)
        val yVelocity = velocityTracker!!.getYVelocity(activePointerId)
        if (yVelocity / maxFlingVelocity > FLING_VELOCITY_THRESHOLD) {
          currentState = HIDDEN
          val timeInSeconds = abs((parentHeight - bottomSheet!!.top) / yVelocity)
          slideAnimator.duration = (timeInSeconds * 1000).toLong()
          slideAnimator.setIntValues(bottomSheet!!.top, parentHeight)
          slideAnimator.start()
        } else {
          val middlePoint = parentHeight - slideRange * 0.65
          val endY = if (bottomSheet!!.top < middlePoint) {
            currentState = SHOWN
            parentHeight - slideRange
          } else {
            currentState = HIDDEN
            parentHeight
          }
          slideAnimator.setIntValues(bottomSheet!!.top, endY)
          slideAnimator.duration = DURATION_DEFAULT
          slideAnimator.start()
        }
        endTouch()
      }
      ACTION_CANCEL -> {
        endTouch()
      }
    }
    if (velocityTracker != null) {
      velocityTracker!!.addMovement(event)
    }
    return true
  }
  
  private fun updateDyOffset(dy: Int) {
    bottomSheetOffsetHelper!!.updateDyOffset(dy)
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
  
  companion object {
    private const val FLING_VELOCITY_THRESHOLD = 0.18f
  }
}