package core.viewbuilding

import com.arsvechkarev.core.R
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.dimen

object Dimens {
  
  private val textSizes = FloatArray(2) { 0f }
  
  val GradientHeaderCurveSize: Float get() = textSizes[0]
  val DividerHeight: Float get() = textSizes[1]
  
  val LogoIconSize get() = 90.dp
  val GradientHeaderHeight get() = 120.dp
  val ErrorLayoutImageSize get() = 90.dp
  val ErrorLayoutTextPadding get() = 32.dp
  val ImageDrawerMargin get() = 16.dp
  val ProgressBarSize get() = 50.dp
  val ProgressBarSizeBig get() = 60.dp
  val CheckmarkHeight get() = ProgressBarSize
  val CheckmarkWidth get() = (CheckmarkHeight * 1.333f).toInt()
  
  init {
    textSizes[0] = dimen(R.dimen.gradient_header_curve_size)
    textSizes[1] = dimen(R.dimen.divider_height)
  }
}