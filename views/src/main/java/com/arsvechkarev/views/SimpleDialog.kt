package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.FrameLayout
import com.arsvechkarev.viewdsl.AccelerateDecelerateInterpolator
import com.arsvechkarev.viewdsl.DURATION_MEDIUM
import com.arsvechkarev.viewdsl.cancelIfRunning
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.visible
import core.extenstions.assertThat
import core.extenstions.f
import core.extenstions.happenedIn
import core.extenstions.lerpColor

class SimpleDialog @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  
  private lateinit var dialogView: View
  private var wasNoMoveEvent = false
  private var currentShadowFraction = 0f
  private val shadowAnimator = ValueAnimator().apply {
    interpolator = AccelerateDecelerateInterpolator
    duration = DURATION_MEDIUM
    addUpdateListener {
      currentShadowFraction = it.animatedValue as Float
      val color = lerpColor(Color.TRANSPARENT, COLOR_SHADOW, currentShadowFraction)
      setBackgroundColor(color)
    }
  }
  
  var isOpened = false
    private set
  
  init {
    gone()
  }
  
  override fun onFinishInflate() {
    super.onFinishInflate()
    assertThat(childCount == 1) { "Only one child for dialog is allowed" }
    dialogView = getChildAt(0)
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    dialogView.translationY = getTranslationForDialogView()
    dialogView.scaleX = getScaleXDialogView()
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
          .alpha(1f)
          .translationY(0f)
          .setDuration(DURATION_MEDIUM)
          .setInterpolator(AccelerateDecelerateInterpolator)
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
          .scaleX(getScaleXDialogView())
          .translationY(getTranslationForDialogView())
          .setDuration((DURATION_MEDIUM * 0.8).toLong())
          .setInterpolator(AccelerateDecelerateInterpolator)
          .withEndAction { gone() }
          .start()
    }
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
        if (wasNoMoveEvent && !(event happenedIn dialogView)) {
          hide()
          return true
        }
      }
    }
    return false
  }
  
  private fun getTranslationForDialogView(): Float = dialogView.measuredHeight.f
  
  private fun getScaleXDialogView(): Float = 0.8f
  
  companion object {
    
    const val COLOR_SHADOW = 0x70000000
  }
}
