@file:Suppress("NOTHING_TO_INLINE")

package viewdsl

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

fun TextView.drawableStart(@DrawableRes drawableRes: Int) {
  val drawable = context.getDrawable(drawableRes)
  val arr = compoundDrawables
  if (isLayoutLeftToRight) {
    setCompoundDrawablesWithIntrinsicBounds(drawable, arr[1], arr[2], arr[3])
  } else {
    setCompoundDrawablesWithIntrinsicBounds(arr[0], arr[1], drawable, arr[3])
  }
}

inline fun TextView.textSize(size: Float) {
  setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
}

inline fun TextView.textSize(@DimenRes dimenRes: Int) {
  textSize = context.dimen(dimenRes)
}

inline fun TextView.text(@StringRes resId: Int) {
  setText(resId)
}

inline fun TextView.text(text: CharSequence) {
  setText(text)
}

inline fun TextView.textColor(color: Int) {
  setTextColor(color)
}

inline fun TextView.drawablePadding(padding: Int) {
  compoundDrawablePadding = padding
}

inline fun TextView.font(font: Typeface) {
  typeface = font
}

inline fun TextView.gravity(gravity: Int) {
  this.gravity = gravity
}