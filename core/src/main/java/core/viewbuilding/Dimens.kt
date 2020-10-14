package core.viewbuilding

import android.content.Context
import com.arsvechkarev.core.R
import core.extenstions.dimen

object Dimens {
  
  private val textSizes = FloatArray(2) { 0f }
  
  val GradientHeaderCurveSize: Float get() = textSizes[0]
  val DividerHeight: Float get() = textSizes[1]
  
  fun init(context: Context) {
    textSizes[0] = context.dimen(R.dimen.gradient_header_curve_size)
    textSizes[1] = context.dimen(R.dimen.divider_height)
  }
}