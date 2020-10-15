package core.viewbuilding

import com.arsvechkarev.core.R
import viewdsl.dimen

object Dimens {
  
  private val textSizes = FloatArray(2) { 0f }
  
  val GradientHeaderCurveSize: Float get() = textSizes[0]
  val DividerHeight: Float get() = textSizes[1]
  
  init {
    textSizes[0] = dimen(R.dimen.gradient_header_curve_size)
    textSizes[1] = dimen(R.dimen.divider_height)
  }
}