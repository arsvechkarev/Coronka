package com.arsvechkarev.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.cancelIfRunning
import config.AnimationsConfigurator
import core.extenstions.execute
import core.extenstions.i
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

class EarthView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
  
  private var itemSize = 40.dp
  private var itemsMargin = 8.dp
  private val wifi = context.getDrawable(R.drawable.ic_wifi_full)!!
  private val earth = context.getDrawable(R.drawable.ic_planet_earth)!!
  
  private val wifiAnimator = ValueAnimator().apply {
    duration = AnimationsConfigurator.DurationMedium
    repeatMode = ValueAnimator.REVERSE
    repeatCount = 4
    addUpdateListener {
      wifi.alpha = it.animatedValue as Int
      invalidate()
    }
  }
  
  fun animateWifi() {
    wifiAnimator.setIntValues(0, 255)
    wifiAnimator.start()
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    if (visibility == VISIBLE) {
      wifi.alpha = 255
    } else {
      wifi.alpha = 0
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val minSide = minOf(w, h)
    val emptyCornerSpace = (minSide * sqrt(2f)) / 2 - minSide / 2
    val extraNeededSpace = (itemSize - emptyCornerSpace).coerceAtLeast(0f).i
    val earthSize = minSide - extraNeededSpace
    val left = (cos(PI / 4) * earthSize / 2 + w / 2 + itemsMargin).toInt()
    wifi.setBounds(left, 0, left + itemSize, itemSize)
    earth.setBounds(w / 2 - earthSize / 2, h / 2 - earthSize / 2,
      w / 2 + earthSize / 2, h / 2 + earthSize / 2)
  }
  
  override fun onDraw(canvas: Canvas) {
    earth.draw(canvas)
    canvas.execute {
      canvas.rotate(45f, wifi.bounds.exactCenterX(), wifi.bounds.exactCenterY())
      wifi.draw(canvas)
    }
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    wifiAnimator.cancelIfRunning()
  }
}