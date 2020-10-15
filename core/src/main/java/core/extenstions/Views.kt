package core.extenstions

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

operator fun View.contains(ev: MotionEvent): Boolean {
  val x = ev.x
  val y = ev.y
  return x >= left && y >= top && x <= right && y <= bottom
}

fun View.heightWithMargins(): Int {
  val params = layoutParams as ViewGroup.MarginLayoutParams
  return measuredHeight + params.topMargin + params.bottomMargin
}