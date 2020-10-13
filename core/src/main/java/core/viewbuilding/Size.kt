package core.viewbuilding

import android.content.Context
import android.view.ViewGroup
import core.extenstions.dimen

sealed class Size {
  
  object MATCH : Size()
  object WRAP : Size()
  class IntValue(val size: Int) : Size()
  class Dimen(val dimenRes: Int) : Size()
  
  companion object {
    
    val MatchContent get() = MATCH
    val WrapContent get() = WRAP
  }
}

internal fun Context.createLayoutParams(width: Size, height: Size): ViewGroup.LayoutParams {
  val widthValue: Int = determineSize(width)
  val heightValue: Int = determineSize(height)
  return ViewGroup.LayoutParams(widthValue, heightValue)
}

private fun Context.determineSize(size: Size): Int {
  return when (size) {
    Size.MATCH -> ViewGroup.LayoutParams.MATCH_PARENT
    Size.WRAP -> ViewGroup.LayoutParams.MATCH_PARENT
    is Size.IntValue -> size.size
    is Size.Dimen -> dimen(size.dimenRes).toInt()
  }
}
