package core.viewbuilding

import com.arsvechkarev.core.R
import viewdsl.Ints.dp
import viewdsl.dimen

object Dimens {
  
  private val textSizes = FloatArray(2) { 0f }
  
  val GradientHeaderCurveSize: Float get() = textSizes[0]
  val DividerHeight: Float get() = textSizes[1]
  
  val GradientHeaderHeight get() = 120.dp
  val ErrorLayoutImageHeight get() = 120.dp
  val ErrorLayoutImageMargin get() = 24.dp
  val ImageDrawerMargin get() = 16.dp
  val ProgressBarSize get() = 40.dp
  
  init {
    textSizes[0] = dimen(R.dimen.gradient_header_curve_size)
    textSizes[1] = dimen(R.dimen.divider_height)
  }
}