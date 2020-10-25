package com.arsvechkarev.views.progressbar

import android.content.Context
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.arsvechkarev.views.progressbar.ProgressBar.Thickness.NORMAL
import core.viewbuilding.Colors.Accent
import android.widget.ProgressBar as AndroidProgressBar

class ProgressBar(
  context: Context,
  color: Int,
  thickness: Thickness,
) : FrameLayout(context) {
  
  constructor(context: Context) : this(context, Accent, NORMAL)
  
  constructor(context: Context, attrs: AttributeSet) : this(context, Accent, NORMAL)
  
  init {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
      addView(MaterialProgressBar(context, color, thickness))
    } else {
      val progressBar = AndroidProgressBar(context).apply {
        isIndeterminate = true
        indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, SRC_ATOP)
      }
      addView(progressBar)
    }
  }
  
  enum class Thickness {
    NORMAL, THICK
  }
}