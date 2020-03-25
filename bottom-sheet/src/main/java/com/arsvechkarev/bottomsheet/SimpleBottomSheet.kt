package com.arsvechkarev.bottomsheet

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color.WHITE
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.arsvechkarev.bottomsheet.R.styleable.SimpleBottomSheet
import com.arsvechkarev.bottomsheet.SimpleBottomSheet.Direction.DOWN
import com.arsvechkarev.bottomsheet.SimpleBottomSheet.Direction.UP
import kotlin.math.abs


class SimpleBottomSheet @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener, OnGestureListener {
  
  companion object {
    const val FLING_VELOCITY_THRESHOLD = 0.18f
    const val FLING_DURATION_COEFFICIENT = 20
    const val SLIDE_ANIMATION_DURATION = 250L
  }
  
  private val cornerRadius: Float
  private val backgroundColor: Int
  
  private val maxVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
  private val gestureDetector: GestureDetector = GestureDetector(context, this)
  
  private var topY = -1f
  private var expandedHalfY = -1f
  private var bottomY = -1f
  
  // for on touch listener
  private var initialY = -1f
  private var lastY = -1f
  
  init {
    val typedArray = context.obtainStyledAttributes(attrs, SimpleBottomSheet, defStyleAttr, 0)
    cornerRadius = typedArray.getDimension(R.styleable.SimpleBottomSheet_cornerRadius, dp(16))
    backgroundColor = typedArray.getColor(R.styleable.SimpleBottomSheet_backgroundColor, WHITE)
    val shapeDrawable = ShapeDrawable(
      RoundRectShape(
        floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0f, 0f, 0f, 0f),
        null,
        null
      )
    )
    shapeDrawable.colorFilter = PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_ATOP)
    background = shapeDrawable
    typedArray.recycle()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    bottomY = (parent as ViewGroup).height.toFloat()
    topY = y
    y = bottomY
    expandedHalfY = bottomY - (bottomY - topY) / 2
    setOnTouchListener(this)
  }
  
  fun show() {
    post { animate().y(topY).start() }
  }
  
  fun hide() {
    post { animate().y(bottomY).start() }
  }
  
  override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
    val normalizedVelocityY = abs(velocityY) / maxVelocity
    if (normalizedVelocityY > FLING_VELOCITY_THRESHOLD) {
      when {
        e2.rawY > e1.rawY -> onSwipe(DOWN, velocityY)
        e2.rawY < e1.rawY -> onSwipe(UP, velocityY)
      }
      return true
    }
    return false
  }
  
  
  override fun onTouch(view: View, event: MotionEvent): Boolean {
    if (gestureDetector.onTouchEvent(event)) {
      return true
    }
    when (event.action) {
      ACTION_DOWN -> {
        initialY = view.y
        lastY = event.rawY
        return true
      }
      ACTION_MOVE -> {
        val y = event.rawY
        var newY = view.y + (y - lastY)
        if (newY < topY) newY = topY
        view.y = newY
        view.invalidate()
        lastY = y
        return true
      }
      ACTION_UP, ACTION_CANCEL -> {
        val endValue = if (view.y > expandedHalfY) bottomY else topY
        ObjectAnimator.ofFloat(this, View.Y, endValue).apply {
          interpolator = AccelerateDecelerateInterpolator()
          duration = SLIDE_ANIMATION_DURATION
          start()
        }
        return performClick()
      }
    }
    return false
  }
  
  private fun onSwipe(direction: Direction, velocityY: Float) {
    val time: Float
    val endY: Float
    if (direction == DOWN) {
      time = abs(bottomY - y / velocityY) / FLING_DURATION_COEFFICIENT
      endY = bottomY
      val animator = ObjectAnimator.ofFloat(this, View.Y, endY)
          .setDuration(time.toLong())
      animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
          println("qwerty: bottomY = $bottomY")
          println("qwerty: viewY = $y")
        }
      })
      animator.start()
    }
  }
  
  enum class Direction {
    UP, DOWN
  }
  
  private fun dp(value: Int) = resources.displayMetrics.density * value
}