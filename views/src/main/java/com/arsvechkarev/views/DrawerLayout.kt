package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.arsvechkarev.views.DrawerLayout.DrawerState.CLOSED
import com.arsvechkarev.views.DrawerLayout.DrawerState.OPENED
import core.HostActivity.DrawerOpenCloseListener
import core.extenstions.AccelerateDecelerateInterpolator
import core.extenstions.assertThat
import core.extenstions.cancelIfRunning
import core.extenstions.doOnEnd
import core.extenstions.execute
import core.extenstions.f
import core.extenstions.withAlpha
import kotlin.math.abs
import kotlin.math.hypot

class DrawerLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
  
  private lateinit var mainView: View
  private lateinit var drawerView: View
  
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  
  private var currentState = CLOSED
  private var isBeingDragged = false
  private var outsideOnDrawerDown = false
  private var outsideOnDrawerDownY = 0f
  private var outsideOnDrawerDownX = 0f
  private var latestX = 0f
  private var velocityTracker: VelocityTracker? = null
  private var slideRange = 0
  
  private val drawerViewAnimator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator
    addUpdateListener { moveDrawer(it.animatedValue as Int) }
  }
  
  private val openCloseListeners = ArrayList<DrawerOpenCloseListener>()
  
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
    assertThat(childCount == 2) { "Layout must have exactly 2 children" }
    mainView = getChildAt(0)
    mainView.measure(widthMeasureSpec, heightMeasureSpec)
    drawerView = getChildAt(1)
    slideRange = (widthSize * SLIDE_RANGE_COEFFICIENT).toInt()
        .coerceAtMost(widthSize)
    val widthSpec = MeasureSpec.makeMeasureSpec(slideRange, MeasureSpec.EXACTLY)
    drawerView.measure(widthSpec, heightMeasureSpec)
    setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    mainView.layout(mainView.left, 0, mainView.measuredWidth, mainView.measuredHeight)
    val drawerLeft = when (currentState) {
      OPENED -> 0
      CLOSED -> -slideRange
    }
    drawerView.layout(drawerLeft, 0, drawerLeft + drawerView.measuredWidth, height)
  }
  
  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    if (!respondToTouches) return false
    when (event.action) {
      ACTION_DOWN -> {
        handleDownOutsideEvent(event)
        latestX = event.x
        initVelocityTrackerIfNeeded()
        velocityTracker?.addMovement(event)
      }
      ACTION_MOVE -> {
        val x = event.x.toInt()
        val xDiff = abs(latestX - x)
        if (xDiff > touchSlop * TOUCH_SLOP_MULTIPLIER) {
          velocityTracker?.addMovement(event)
          isBeingDragged = true
          latestX = event.x
        }
      }
      ACTION_UP, ACTION_CANCEL -> {
        if (outsideOnDrawerDown) {
          handleUpOutsideEvent(event)
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
    when (event.action) {
      ACTION_DOWN -> {
        handleDownOutsideEvent(event)
        latestX = event.x
        velocityTracker?.addMovement(event)
      }
      ACTION_MOVE, ACTION_UP -> {
        velocityTracker?.addMovement(event)
        val distance = event.x - latestX
        var newLeft = drawerView.left + distance.toInt()
        if (newLeft >= 0) newLeft = 0
        moveDrawer(newLeft)
        latestX = event.x
        invalidate()
        if (event.action == ACTION_UP) {
          if (outsideOnDrawerDown) {
            handleUpOutsideEvent(event)
          }
          handleUpEvent()
        }
      }
    }
    return true
  }
  
  private fun handleDownOutsideEvent(event: MotionEvent) {
    if (currentState == OPENED && event.x > drawerView.right) {
      outsideOnDrawerDownX = event.x
      outsideOnDrawerDownY = event.y
      outsideOnDrawerDown = true
    }
  }
  
  private fun handleUpOutsideEvent(event: MotionEvent) {
    outsideOnDrawerDown = false
    val xDist = abs(event.x - outsideOnDrawerDownX)
    val yDist = abs(event.y - outsideOnDrawerDownY)
    if (hypot(xDist, yDist) < touchSlop) {
      close()
    }
  }
  
  private fun handleUpEvent() {
    velocityTracker!!.computeCurrentVelocity(1000)
    val xVelocity = velocityTracker!!.xVelocity
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
  }
  
  override fun dispatchDraw(canvas: Canvas) {
    canvas.execute {
      translate(mainView.left.f, 0f)
      mainView.draw(canvas)
    }
    val fraction = (drawerView.right.toFloat() / slideRange).coerceAtLeast(0f)
    canvas.drawColor(Color.BLACK.withAlpha(fraction * SHADOW_ALPHA_COEFFICIENT))
    canvas.execute {
      translate(drawerView.left.f, 0f)
      drawerView.draw(canvas)
    }
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
    const val SLIDE_RANGE_COEFFICIENT = 0.75f
    const val FLING_VELOCITY_THRESHOLD = 0.18f
    
    const val DEFAULT_DURATION = 250L
    const val TOUCH_SLOP_MULTIPLIER = 2
  }
}