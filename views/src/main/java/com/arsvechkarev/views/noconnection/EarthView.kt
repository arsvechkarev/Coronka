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
import core.extenstions.dpInt
import core.extenstions.i
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

class EarthView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private var wifiSize = 40.dpInt
  private var itemsMargin = 8.dpInt
  private val wifi = context.getDrawable(R.drawable.ic_wifi_full)!!
  private val earth = context.getDrawable(R.drawable.ic_planet_earth)!!
  
  private val animator = ValueAnimator.ofArgb(Color.WHITE, Color.GRAY).apply {
    duration = DURATION_MEDIUM
    repeatMode = REVERSE
    repeatCount = 5
    addUpdateListener {
      wifi.colorFilter = PorterDuffColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC_ATOP)
      invalidate()
    }
  }
  
  fun animateWifi() {
    animator.start()
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val minSide = minOf(w, h)
    val emptyCornerSpace = (minSide * sqrt(2f)) / 2 - minSide / 2
    val extraNeededSpace = (wifiSize - emptyCornerSpace).coerceAtLeast(0f).i
    val earthSize = minSide - extraNeededSpace
    val left = (cos(PI / 4) * earthSize / 2 + w / 2 + itemsMargin).toInt()
    wifi.setBounds(left, 0, left + wifiSize, wifiSize)
    earth.setBounds(w / 2 - earthSize / 2, h / 2 - earthSize / 2,
      w / 2 + earthSize / 2, h / 2 + earthSize / 2)
  }
  
  override fun onDraw(canvas: Canvas) {
    earth.draw(canvas)
    canvas.save()
    canvas.rotate(45f, wifi.bounds.exactCenterX(), wifi.bounds.exactCenterY())
    wifi.draw(canvas)
    canvas.restore()
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    animator.cancelIfRunning()
  }
}