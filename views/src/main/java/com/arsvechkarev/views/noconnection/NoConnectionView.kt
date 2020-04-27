package com.arsvechkarev.views.noconnection

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.arsvechkarev.views.R
import core.extenstions.assertThat
import core.extenstions.cancelIfRunning

class NoConnectionView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {
  
  private val wifi = context.getDrawable(R.drawable.ic_wifi_full)!!.mutate()
  private val hourglass = context.getDrawable(R.drawable.ic_hourglass)!!.mutate()
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
    hourglassAnimator.cancelIfRunning()
    currentItem = wifi
    wifiAnimator.start()
  }
  
  fun animateHourglass() {
    wifiAnimator.cancelIfRunning()
    currentItem = hourglass
    hourglassAnimator.start()
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    if (visibility == VISIBLE) {
      wifi.alpha = 255
      hourglass.alpha = 255
      hourglassRotation = 0f
    } else {
      wifi.alpha = 0
      hourglass.alpha = 0
      currentItem?.alpha = 0
    }
  }
  
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    wifi.setBounds(0, 0, w, h)
    hourglass.setBounds(0, 0, w, h)
  }
  
  override fun onDraw(canvas: Canvas) {
    canvas.save()
    currentItem ?: return
    if (currentItem == hourglass) {
      canvas.rotate(hourglassRotation, width / 2f, height / 2f)
      currentItem!!.draw(canvas)
    } else {
      assertThat(currentItem == wifi)
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