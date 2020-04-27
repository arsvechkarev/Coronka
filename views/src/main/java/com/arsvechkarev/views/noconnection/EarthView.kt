package com.arsvechkarev.views.noconnection

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.views.R
import core.extenstions.assertThat
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
  
  private var itemSize = 40.dpInt
  private var itemsMargin = 8.dpInt
  private val wifi = context.getDrawable(R.drawable.ic_wifi_full)!!
  private val hourglass = context.getDrawable(R.drawable.ic_hourglass)!!
  private val earth = context.getDrawable(R.drawable.ic_planet_earth)!!
  private var currentItem: Drawable? = null
  private var hourglassRotation = 0f
  
  private val wifiAnimator = createWifiAnimator { alpha ->
    wifi.alpha = alpha
    invalidate()
  }
  
  private val hourglassAnimator = createHourglassAnimator { hourglassAlpha ->
    hourglass.alpha = hourglassAlpha
    hourglassRotation = animatedValue as Float
    invalidate()
  }
  
  fun animateWifi() {
    currentItem = wifi
    wifiAnimator.start()
  }
  
  fun animateHourglass() {
    currentItem = hourglass
    hourglassAnimator.start()
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    super.onVisibilityChanged(changedView, visibility)
    if (visibility != VISIBLE) {
      wifi.alpha = 0
      hourglass.alpha = 0
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    val minSide = minOf(w, h)
    val emptyCornerSpace = (minSide * sqrt(2f)) / 2 - minSide / 2
    val extraNeededSpace = (itemSize - emptyCornerSpace).coerceAtLeast(0f).i
    val earthSize = minSide - extraNeededSpace
    val left = (cos(PI / 4) * earthSize / 2 + w / 2 + itemsMargin).toInt()
    wifi.setBounds(left, 0, left + itemSize, itemSize)
    hourglass.setBounds(left, 0, left + itemSize, itemSize)
    earth.setBounds(w / 2 - earthSize / 2, h / 2 - earthSize / 2,
      w / 2 + earthSize / 2, h / 2 + earthSize / 2)
  }
  
  override fun onDraw(canvas: Canvas) {
    earth.draw(canvas)
    currentItem ?: return
    canvas.save()
    if (currentItem == wifi) {
      canvas.rotate(45f, wifi.bounds.exactCenterX(), wifi.bounds.exactCenterY())
      currentItem!!.draw(canvas)
    } else {
      assertThat(currentItem == hourglass)
      canvas.rotate(hourglassRotation, hourglass.bounds.exactCenterX(),
        hourglass.bounds.exactCenterY())
      currentItem!!.draw(canvas)
    }
    canvas.restore()
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    wifiAnimator.cancelIfRunning()
    hourglassAnimator.cancelIfRunning()
  }
}