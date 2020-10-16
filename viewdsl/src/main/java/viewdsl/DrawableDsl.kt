package viewdsl

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.View

fun View.rippleBackground(ripple: Int) {
  val rectShape = RectShape()
  val background = ShapeDrawable().apply {
    shape = rectShape
    paint.color = Color.TRANSPARENT
  }
  val mask = ShapeDrawable().apply {
    shape = rectShape
    paint.color = ripple
  }
  isClickable = true
  isFocusable = true
  background(RippleDrawable(ColorStateList.valueOf(ripple), background, mask))
}

fun View.clearDrawable() {
  background = null
}