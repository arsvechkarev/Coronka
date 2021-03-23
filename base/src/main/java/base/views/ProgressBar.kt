package base.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import base.R
import base.resources.Colors.Accent
import base.views.ProgressBar.Thickness.NORMAL
import base.views.ProgressBar.Thickness.THICK
import com.arsvechkarev.viewdsl.startIfNotRunning
import com.arsvechkarev.viewdsl.stopIfRunning

class ProgressBar(
  context: Context,
  color: Int,
  thickness: Thickness,
) : FrameLayout(context) {
  
  private val drawable get() = background as AnimatedVectorDrawable
  
  constructor(context: Context) : this(context, Accent, NORMAL)
  
  constructor(context: Context, attrs: AttributeSet) : this(context, Accent, NORMAL)
  
  init {
    background = when (thickness) {
      NORMAL -> context.getDrawable(R.drawable.progress_anim_normal)!!
      THICK -> context.getDrawable(R.drawable.progress_anim_thick)!!
    }.apply {
      colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
  }
  
  override fun onVisibilityChanged(changedView: View, visibility: Int) {
    if (visibility == VISIBLE) {
      drawable.startIfNotRunning()
    } else {
      drawable.stopIfRunning()
    }
  }
  
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    drawable.startIfNotRunning()
  }
  
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    drawable.stopIfRunning()
  }
  
  enum class Thickness {
    NORMAL, THICK
  }
}