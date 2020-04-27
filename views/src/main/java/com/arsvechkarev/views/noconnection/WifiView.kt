package com.arsvechkarev.views.noconnection

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.views.R
import core.extenstions.DURATION_MEDIUM
import core.extenstions.cancelIfRunning

class WifiView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val wifi = context.getDrawable(
    R.drawable.ic_wifi_full)!!
  private val primaryColor = Color.WHITE
  private val secondaryColor = Color.GRAY
  
  private val animator = ValueAnimator.ofArgb(primaryColor, secondaryColor).apply {
    duration = DURATION_MEDIUM
    repeatCount = 3
    repeatMode = REVERSE
    addUpdateListener {
      wifi.colorFilter = PorterDuffColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC_ATOP)
      invalidate()
    }
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    if (visibility == GONE || visibility == INVISIBLE) {
      wifi.colorFilter = PorterDuffColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP)
      animator.cancelIfRunning()
    }
  }
  
  fun animateColorChanging() {
    animator.start()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    wifi.setBounds(0, 0, w, h)
  }
  
  override fun onDraw(canvas: Canvas) {
    wifi.draw(canvas)
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    animator.cancelIfRunning()
  }
}