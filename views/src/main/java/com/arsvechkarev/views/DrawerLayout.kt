package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
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
import android.view.ViewGroup
import com.arsvechkarev.viewdsl.AccelerateDecelerateInterpolator
import com.arsvechkarev.viewdsl.cancelIfRunning
import com.arsvechkarev.viewdsl.doOnEnd
import com.arsvechkarev.viewdsl.isOrientationPortrait
import com.arsvechkarev.views.DrawerLayout.DrawerState.CLOSED
import com.arsvechkarev.views.DrawerLayout.DrawerState.OPENED
import core.HostActivity.DrawerOpenCloseListener
import core.INVALID_POINTER
import core.extenstions.assertThat
import kotlin.math.abs
import kotlin.math.hypot

class DrawerLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
  
  private val mainView: View get() = getChildAt(0)
  private val drawerView: View get() = getChildAt(2)
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  
  private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  
  private var isHoldingFinger = false
  private var currentState = CLOSED
  private var isBeingDragged = false
  private var outsideOnDrawerDown = false
  private var outsideOnDrawerDownY = 0f
  private var outsideOnDrawerDownX = 0f
  private var latestX = 0f
  private var velocityTracker: VelocityTracker? = null
  private var slideRange = 0
  private var activePointerId = INVALID_POINTER
  
  private val drawerViewAnimator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator
    addUpdateListener { moveDrawer(it.animatedValue as Int) }
  }
  
  private val openCloseListeners = ArrayList<DrawerOpenCloseListener>()
  
  private val dummyView get() = getChildAt(1)
  
  var respondToTouches = true
  
  fun addOpenCloseListener(listener: DrawerOpenCloseListener) {
    openCloseListeners.add(listener)
  }
  
  fun removeOpenCloseListener(listener: DrawerOpenCloseListener) {
    openCloseListeners.remove(listener)
  }
  
  fun open() {
    currentState = OPENED
    post {
      drawerViewAnimator.duration = DEFAULT_DURATION
      drawerViewAnimator.setIntValues(drawerView.left, 0)
      drawerViewAnimator.doOnEnd {
        openCloseListeners.forEach { it.onDrawerOpened() }
      }
      drawerViewAnimator.start()
    }
  }
  
  fun close(notifyListeners: Boolean = true, andThen: () -> Unit = {}) {
    currentState = CLOSED
    post {
      drawerViewAnimator.duration = DEFAULT_DURATION
      drawerViewAnimator.setIntValues(drawerView.left, -slideRange)
      drawerViewAnimator.doOnEnd {
        if (notifyListeners) {
          openCloseListeners.forEach { it.onDrawerClosed() }
        }
        andThen()
      }
      drawerViewAnimator.start()
    }
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    mainView.measure(widthMeasureSpec, heightMeasureSpec)
    dummyView.measure(widthMeasureSpec, heightMeasureSpec)
    slideRange = computeSlideRange(widthSize)
    val widthSpec = MeasureSpec.makeMeasureSpec(slideRange, MeasureSpec.EXACTLY)
    drawerView.measure(widthSpec, heightMeasureSpec)
    setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
  }
  
  private fun computeSlideRange(width: Int): Int {
    val coefficient = if (isOrientationPortrait) {
      PORTRAIT_SLIDE_RANGE_COEFFICIENT
    } else {
      LANDSCAPE_SLIDE_RANGE_COEFFICIENT
    }
    return (width * coefficient).toInt().coerceAtMost(width)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val left = mainView.left
    mainView.layout(left, 0, left + mainView.measuredWidth, b - t)
    dummyView.layout(left, 0, left + dummyView.measuredWidth, b - t)
    slideRange = computeSlideRange(r - l)
    var drawerLeft = when (currentState) {
      OPENED -> 0
      CLOSED -> -slideRange
    }
    if (isHoldingFinger) {
      drawerLeft = drawerView.left
    }
    drawerView.layout(drawerLeft, 0, drawerLeft + drawerView.measuredWidth, height)
  }
  
  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    if (!respondToTouches) return false
    when (event.actionMasked) {
      ACTION_DOWN -> {
        if (handleDownOutsideEvent(event)) return true
        initVelocityTrackerIfNeeded()
        activePointerId = event.getPointerId(0)
        latestX = event.x
      }
      ACTION_POINTER_DOWN -> {
        activePointerId = event.getPointerId(event.actionIndex)
        latestX = event.getX(event.actionIndex)
      }
      ACTION_MOVE -> {
        val pointerIndex: Int = event.findPointerIndex(activePointerId)
        if (pointerIndex < 0) {
          return false
        }
        val x = event.getX(pointerIndex).toInt()
        val xDiff = abs(latestX - x)
        if (xDiff > touchSlop * TOUCH_SLOP_MULTIPLIER) {
          isHoldingFinger = true
          velocityTracker?.addMovement(event)
          isBeingDragged = true
          latestX = event.getX(pointerIndex)
        }
      }
      ACTION_POINTER_UP -> {
        onPointerUp(event)
      }
      ACTION_UP, ACTION_CANCEL -> {
        if (outsideOnDrawerDown && handleUpOutsideEvent(event)) {
          return true
        }
        recycleVelocityTracker()
        isBeingDragged = false
      }
    }
    return isBeingDragged
  }
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!respondToTouches) return false
    initVelocityTrackerIfNeeded()
    when (event.actionMasked) {
      ACTION_DOWN -> {
        if (handleDownOutsideEvent(event)) return true
        activePointerId = event.getPointerId(0)
        isHoldingFinger = true
        latestX = event.x
        velocityTracker?.addMovement(event)
      }
      ACTION_POINTER_DOWN -> {
        activePointerId = event.getPointerId(event.actionIndex)
        latestX = event.getX(event.actionIndex)
      }
      ACTION_MOVE -> {
        val pointerIndex: Int = event.findPointerIndex(activePointerId)
        if (pointerIndex < 0) {
          return false
        }
        velocityTracker?.addMovement(event)
        val x = event.getX(pointerIndex)
        val distance = x - latestX
        var newLeft = drawerView.left + distance.toInt()
        if (newLeft >= 0) newLeft = 0
        moveDrawer(newLeft)
        latestX = event.getX(pointerIndex)
        invalidate()
      }
      ACTION_POINTER_UP -> {
        onPointerUp(event)
      }
      ACTION_UP, ACTION_CANCEL -> {
        isHoldingFinger = false
        if (outsideOnDrawerDown && handleUpOutsideEvent(event)) {
          return true
        }
        handleUpEvent()
      }
    }
    return true
  }
  
  private fun onPointerUp(event: MotionEvent) {
    val actionIndex = event.actionIndex
    if (event.getPointerId(actionIndex) == activePointerId) {
      val newIndex = if (actionIndex == 0) 1 else 0
      activePointerId = event.getPointerId(newIndex)
      latestX = event.getX(newIndex)
    }
  }
  
  
  private fun handleDownOutsideEvent(event: MotionEvent): Boolean {
    if (currentState == OPENED && event.x > drawerView.right) {
      outsideOnDrawerDownX = event.x
      outsideOnDrawerDownY = event.y
      outsideOnDrawerDown = true
      return true
    }
    return false
  }
  
  private fun handleUpOutsideEvent(event: MotionEvent): Boolean {
    outsideOnDrawerDown = false
    val xDist = abs(event.x - outsideOnDrawerDownX)
    val yDist = abs(event.y - outsideOnDrawerDownY)
    if (hypot(xDist, yDist) < touchSlop) {
      close()
      return true
    }
    return false
  }
  
  private fun handleUpEvent() {
    latestX = Float.MIN_VALUE
    velocityTracker!!.computeCurrentVelocity(1000)
    val xVelocity = velocityTracker!!.getXVelocity(activePointerId)
    val flingToClose = xVelocity < 0 && currentState == OPENED
    val flingToOpen = xVelocity > 0 && currentState == CLOSED
    if (abs(xVelocity) / maxFlingVelocity > FLING_VELOCITY_THRESHOLD
        && (flingToClose || flingToOpen)) {
      val timeInSeconds = when {
        flingToClose -> drawerView.right / abs(xVelocity)
        flingToOpen -> (slideRange - drawerView.right) / xVelocity
        else -> throw IllegalStateException()
      }
      drawerViewAnimator.duration = (timeInSeconds * 900).toLong()
    } else {
      drawerViewAnimator.duration = DEFAULT_DURATION
    }
    val endX = when {
      drawerView.right < slideRange * 0.15f -> -slideRange
      drawerView.right > slideRange * 0.85f -> 0
      else -> {
        val result = when (currentState) {
          OPENED -> -slideRange
          CLOSED -> 0
        }
        result
      }
    }
    if (endX == 0) {
      drawerViewAnimator.doOnEnd { openCloseListeners.forEach { it.onDrawerOpened() } }
      currentState = OPENED
    } else {
      assertThat(endX == -slideRange)
      drawerViewAnimator.doOnEnd { openCloseListeners.forEach { it.onDrawerClosed() } }
      currentState = CLOSED
    }
    drawerViewAnimator.cancelIfRunning()
    drawerViewAnimator.setIntValues(drawerView.left, endX)
    drawerViewAnimator.start()
    recycleVelocityTracker()
    isBeingDragged = false
  }
  
  private fun moveDrawer(newLeft: Int) {
    drawerView.left = newLeft
    drawerView.right = newLeft + slideRange
    val fraction = (drawerView.right.toFloat() / slideRange).coerceAtLeast(0f)
    val newMainLeft = (fraction * PARALLAX_COEFFICIENT * slideRange).toInt()
    mainView.left = newMainLeft
    mainView.right = newMainLeft + mainView.measuredWidth
    dummyView.alpha = fraction * SHADOW_ALPHA_COEFFICIENT
    dummyView.left = mainView.left
    dummyView.right = mainView.right
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
    return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
  }
  
  override fun generateLayoutParams(p: LayoutParams): LayoutParams {
    return MarginLayoutParams(p)
  }
  
  override fun checkLayoutParams(p: LayoutParams): Boolean {
    return p is MarginLayoutParams
  }
  
  override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
    return MarginLayoutParams(context, attrs)
  }
  
  private fun initVelocityTrackerIfNeeded() {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain()
    }
  }
  
  private fun recycleVelocityTracker() {
    velocityTracker?.recycle()
    velocityTracker = null
  }
  
  private enum class DrawerState {
    OPENED, CLOSED;
  }
  
  private companion object {
  
    const val PARALLAX_COEFFICIENT = 0.3f
    const val SHADOW_ALPHA_COEFFICIENT = 0.7f
    const val PORTRAIT_SLIDE_RANGE_COEFFICIENT = 0.85f
    const val LANDSCAPE_SLIDE_RANGE_COEFFICIENT = 0.6f
    const val FLING_VELOCITY_THRESHOLD = 0.18f
  
    const val DEFAULT_DURATION = 250L
    const val TOUCH_SLOP_MULTIPLIER = 2
  }
}