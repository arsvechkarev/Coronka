package core.viewbuilding

import com.arsvechkarev.core.R
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.dimen

object Dimens {
  
  private val dimenValues = FloatArray(2) { 0f }
  
  val GradientHeaderCurveSize: Float get() = dimenValues[0]
  val DividerHeight: Float get() = dimenValues[1]
  
  val LogoIconSize get() = 90.dp
  val GradientHeaderHeight get() = 120.dp
  val ErrorLayoutImageSize get() = 90.dp
  val ErrorLayoutTextPadding get() = 32.dp
  val ImageDrawerMargin get() = 16.dp
  val ProgressBarSize get() = 50.dp
  
  init {
    dimenValues[0] = dimen(R.dimen.gradient_header_curve_size)
    dimenValues[1] = dimen(R.dimen.divider_height)
  }
}