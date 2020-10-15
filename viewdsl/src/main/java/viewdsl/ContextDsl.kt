package viewdsl

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import java.util.Locale

fun dimen(dimenRes: Int) = ContextHolder.context.dimen(dimenRes)

val isOrientationPortrait: Boolean
  get() = ContextHolder.context.resources.configuration.orientation ==
      Configuration.ORIENTATION_PORTRAIT

val isLayoutLeftToRight: Boolean
  get() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR

@ColorInt
fun Context.getAttrColor(@AttrRes resId: Int): Int {
  val typedValue = TypedValue()
  val resolved = theme.resolveAttribute(resId, typedValue, true)
  require(resolved) { "Attribute cannot be resolved" }
  return typedValue.data
}

fun Context.dimen(@DimenRes resId: Int): Float {
  return resources.getDimension(resId)
}

fun Context.createLayoutParams(
  width: Size,
  height: Size,
  margins: Margins = Margins()
): ViewGroup.LayoutParams {
  val widthValue: Int = determineSize(width)
  val heightValue: Int = determineSize(height)
  val layoutParams = ViewGroup.MarginLayoutParams(widthValue, heightValue)
  layoutParams.setMargins(
    margins.left,
    margins.top,
    margins.right,
    margins.bottom
  )
  return layoutParams
}

fun Context.determineSize(size: Size) = when (size) {
  Size.MATCH_PARENT -> ViewGroup.LayoutParams.MATCH_PARENT
  Size.WRAP_PARENT -> ViewGroup.LayoutParams.WRAP_CONTENT
  is Size.IntValue -> size.size
  is Size.Dimen -> resources.getDimension(size.dimenRes).toInt()
}