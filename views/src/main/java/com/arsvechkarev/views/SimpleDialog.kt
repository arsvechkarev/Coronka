package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import com.arsvechkarev.viewdsl.AccelerateDecelerateInterpolator
import com.arsvechkarev.viewdsl.DURATION_MEDIUM
import com.arsvechkarev.viewdsl.OvershootInterpolator
import com.arsvechkarev.viewdsl.cancelIfRunning
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.visible
import core.extenstions.contains
import core.viewbuilding.Colors

class SimpleDialog @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  
  private var wasNoMoveEvent = false
  private var currentShadowFraction = 0f
  private val dialogView get() = getChildAt(0)
  private val shadowAnimator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator
    duration = DURATION_MEDIUM
    addUpdateListener {
      currentShadowFraction = it.animatedValue as Float
      onShadowFractionChangedListener?.invoke(currentShadowFraction)
      val color = ColorUtils.blendARGB(Color.TRANSPARENT, Colors.Shadow, currentShadowFraction)
      setBackgroundColor(color)
    }
  }
  
  var onShadowFractionChangedListener: ((Float) -> Unit)? = null
  
  var isOpened = false
    private set
  
  init {
    gone()
  }
  
  fun show() {
    if (isOpened) return
    isOpened = true
    post {
      visible()
      dialogView.alpha = 0f
      dialogView.visible()
      shadowAnimator.cancelIfRunning()
      shadowAnimator.setFloatValues(currentShadowFraction, 1f)
      shadowAnimator.start()
      dialogView.animate()
          .withLayer()
          .scaleX(1f)
          .scaleY(1f)
          .alpha(1f)
          .setDuration(DURATION_MEDIUM)
          .setInterpolator(OvershootInterpolator)
          .start()
    }
  }
  
  fun hide() {
    if (!isOpened) return
    isOpened = false
    post {
      shadowAnimator.cancelIfRunning()
      shadowAnimator.setFloatValues(currentShadowFraction, 0f)
      shadowAnimator.start()
      dialogView.animate()
          .withLayer()
          .alpha(0f)
          .scaleX(SCALE_FACTOR)
          .scaleY(SCALE_FACTOR)
          .setDuration((DURATION_MEDIUM * 0.8).toLong())
          .setInterpolator(AccelerateDecelerateInterpolator)
          .withEndAction(::gone)
          .start()
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    dialogView.scaleX = SCALE_FACTOR
    dialogView.scaleY = SCALE_FACTOR
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
    when (event.action) {
      ACTION_DOWN -> {
        wasNoMoveEvent = true
        return true
      }
      ACTION_MOVE -> {
        wasNoMoveEvent = false
      }
      ACTION_UP -> {
        if (wasNoMoveEvent && event !in dialogView) {
          hide()
          return true
        }
      }
    }
    return false
  }
  
  companion object {
  
    private const val SCALE_FACTOR = 0.8f
  }
}