package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.annotation.SuppressLint
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
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.arsvechkarev.views.BottomSheet.State.HIDDEN
import com.arsvechkarev.views.BottomSheet.State.SHOWN
import core.extenstions.assertThat
import core.extenstions.cancelIfRunning
import core.extenstions.contains
import kotlin.math.abs

class BottomSheet @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {
  
  private var currentState: State
  
  private lateinit var mainView: View
  private lateinit var slideView: View
  
  private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  private val maxFlingVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  private var isBeingDragged = false
  private var wasDownEventInSlideView = false
  private var latestY = -1f
  private var velocityTracker: VelocityTracker? = null
  private var slideRange = 0
  
  private val slideViewAnimator = ValueAnimator().apply {
    addUpdateListener {
      val value = it.animatedValue as Int
      this@BottomSheet.slideView.top = value
    }
  }
  
  init {
    val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.BottomSheet, 0, 0)
    when (attributes.getInt(R.styleable.BottomSheet_defaultState, 0)) {
      0 -> currentState = SHOWN
      1 -> currentState = HIDDEN
      else -> throw IllegalStateException("Unknown default state")
    }
    attributes.recycle()
  }
  
  fun show() {
    currentState = SHOWN
    post {
      slideViewAnimator.duration = DEFAULT_DURATION
      slideViewAnimator.setIntValues(slideView.top, height - slideRange)
      slideViewAnimator.start()
    }
  }
  
  fun hide() {
    currentState = HIDDEN
    post {
      slideViewAnimator.duration = DEFAULT_DURATION
      slideViewAnimator.setIntValues(slideView.top, height)
      slideViewAnimator.start()
    }
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)
    assertThat(childCount == 2) { "Layout must have exactly 2 children" }
    mainView = getChildAt(0)
    slideView = getChildAt(1)
    measureChildWithMargins(mainView, widthMeasureSpec, 0, heightMeasureSpec, 0)
    measureChildWithMargins(slideView, widthMeasureSpec, 0, heightMeasureSpec, 0)
    slideRange = slideView.measuredHeight
    setMeasuredDimension(widthSize, heightSize)
  }
  
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    if (!changed) return
    val parentLeft = l + paddingLeft
    val parentTop = t + paddingTop
    val parentBottom = b - t - paddingBottom
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      val params = child.layoutParams as MarginLayoutParams
      if (child === slideView) {
        val slideViewTop = when (currentState) {
          SHOWN -> parentBottom - slideView.measuredHeight + params.topMargin
          HIDDEN -> b - t
        }
        child.layout(
          parentLeft + params.marginStart,
          slideViewTop,
          parentLeft + slideView.measuredWidth,
          parentBottom - params.bottomMargin
        )
      } else {
        check(child === mainView)
        child.layout(
          parentLeft + params.marginStart,
          parentTop + params.topMargin,
          parentLeft + mainView.measuredWidth,
          parentTop + mainView.measuredHeight
        )
      }
    }
  }
  
  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      ACTION_DOWN -> {
        wasDownEventInSlideView = event in slideView
        if (wasDownEventInSlideView) {
          latestY = event.y
          initVelocityTrackerIfNeeded()
          velocityTracker!!.addMovement(event)
        } else {
          isBeingDragged = false
          recycleVelocityTracker()
        }
      }
      ACTION_MOVE -> {
        if (wasDownEventInSlideView) {
          val y = event.y.toInt()
          val yDiff = abs(latestY - y)
          if (yDiff > touchSlop) {
            velocityTracker!!.addMovement(event)
            isBeingDragged = true
            latestY = event.y
          }
        }
      }
      ACTION_UP, ACTION_CANCEL -> {
        recycleVelocityTracker()
        wasDownEventInSlideView = false
        isBeingDragged = false
      }
    }
    return isBeingDragged
  }
  
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    initVelocityTrackerIfNeeded()
    when (event.actionMasked) {
      ACTION_DOWN -> {
        if (wasDownEventInSlideView) {
          latestY = event.y
          velocityTracker!!.addMovement(event)
        }
      }
      ACTION_MOVE -> {
        if (wasDownEventInSlideView) {
          velocityTracker!!.addMovement(event)
          val distance = event.y - latestY
          var newTop = slideView.top + distance.toInt()
          val maxTop = height - slideRange
          if (newTop <= maxTop) newTop = maxTop
          slideView.top = newTop
          latestY = event.y
          invalidate()
        }
      }
      ACTION_UP -> {
        velocityTracker!!.computeCurrentVelocity(1000)
        val yVelocity = velocityTracker!!.yVelocity
        if (abs(yVelocity) / maxFlingVelocity > FLING_VELOCITY_THRESHOLD) {
          currentState = HIDDEN
          val timeInSeconds = abs((height - slideView.top) / yVelocity)
          slideViewAnimator.duration = (timeInSeconds * 1000).toLong()
          slideViewAnimator.setIntValues(slideView.top, height)
          slideViewAnimator.start()
        } else {
          val middlePoint = height - slideRange * 0.65
          val endY = if (slideView.top < middlePoint) {
            currentState = SHOWN
            height - slideRange
          } else {
            currentState = HIDDEN
            height
          }
          slideViewAnimator.setIntValues(slideView.top, endY)
          slideViewAnimator.duration = DEFAULT_DURATION
          slideViewAnimator.start()
        }
        recycleVelocityTracker()
        isBeingDragged = false
      }
    }
    return true
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
    return MarginLayoutParams(MATCH_PARENT, MATCH_PARENT)
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
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    slideViewAnimator.cancelIfRunning()
  }
  
  private fun initVelocityTrackerIfNeeded() {
    if (velocityTracker == null) {
      velocityTracker = VelocityTracker.obtain()
    }
  }
  
  private fun recycleVelocityTracker() {
    if (velocityTracker != null) {
      velocityTracker!!.recycle()
      velocityTracker = null
    }
  }
  
  private enum class State {
    SHOWN, HIDDEN
  }
  
  private companion object {
    const val DEFAULT_DURATION = 250L
    const val FLING_VELOCITY_THRESHOLD = 0.18f
  }
}
