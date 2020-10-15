package com.arsvechkarev.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.FrameLayout
import core.extenstions.assertThat
import core.extenstions.contains
import viewdsl.DURATION_DEFAULT
import viewdsl.animateColor
import viewdsl.doOnEnd
import viewdsl.gone
import viewdsl.invisible
import viewdsl.visible

class SimpleDialog @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  
  private lateinit var dialogView: View
  private var wasNoMoveEvent = false
  
  init {
    gone()
  }
  
  override fun onFinishInflate() {
    super.onFinishInflate()
    assertThat(childCount == 1) { "Only one child for dialog is allowed" }
    dialogView = getChildAt(0)
    invisible()
  }
  
  fun show() {
    post {
      visible()
      dialogView.alpha = 0f
      dialogView.visible()
      animateColor(0x00000000, 0x70000000)
      val translateAnimation = ObjectAnimator.ofFloat(dialogView, View.TRANSLATION_Y,
        dialogView.height / 3f, 0f)
      val alphaAnimation = ObjectAnimator.ofFloat(dialogView, View.ALPHA, 0f, 1f)
      AnimatorSet().apply {
        duration = DURATION_DEFAULT
        playTogether(translateAnimation, alphaAnimation)
        start()
      }
    }
  }
  
  fun dismiss() {
    post {
      animateColor(0x70000000, 0x00000000, andThen = { gone() })
      val translateAnimation = ObjectAnimator.ofFloat(dialogView, View.TRANSLATION_Y,
        0f, dialogView.height / 3f)
      val alphaAnimation = ObjectAnimator.ofFloat(dialogView, View.ALPHA, 1f, 0f)
      AnimatorSet().apply {
        duration = DURATION_DEFAULT
        playTogether(translateAnimation, alphaAnimation)
        start()
        doOnEnd { gone() }
      }
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
        if (wasNoMoveEvent && event !in dialogView) {
          dismiss()
          return true
        }
      }
    }
    return false
  }
}
