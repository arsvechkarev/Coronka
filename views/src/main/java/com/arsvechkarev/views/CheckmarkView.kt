package com.arsvechkarev.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import com.arsvechkarev.viewdsl.stopIfRunning
import com.arsvechkarev.viewdsl.visible
import core.viewbuilding.Colors

class CheckmarkView(context: Context) : View(context) {
  
  val drawable get() = background as AnimatedVectorDrawable
  
  init {
    background = context.getDrawable(R.drawable.avd_checkmark)
    background.colorFilter = PorterDuffColorFilter(Colors.Checkmark, PorterDuff.Mode.SRC_ATOP)
  }
  
  fun animateCheckmark(andThen: () -> Unit = {}) {
    visible()
    drawable.start()
    invalidate()
    handler.postDelayed(andThen, DELAY)
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    drawable.stopIfRunning()
  }
  
  private companion object {
    
    const val DELAY = 1300L
  }
}