package core.viewbuilding

import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import core.extenstions.assertThat

fun exactly(size: Int): Int {
  return View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
}

fun atMost(size: Int): Int {
  return View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)
}

fun unspecified(size: Int = Int.MAX_VALUE / 2): Int {
  return View.MeasureSpec.makeMeasureSpec(size, UNSPECIFIED)
}

/** Returns minimum size of width and height measure specs */
fun getMinimumSize(widthSpec: Int, heightSpec: Int): Int {
  val width = widthSpec.size
  val height = heightSpec.size
  if (width == 0 || height == 0) {
    if (heightSpec.mode == UNSPECIFIED) {
      assertThat(width > 0)
      return width
    }
  }
  return minOf(width, height)
}

val Int.size: Int
  get() = View.MeasureSpec.getSize(this)

val Int.mode: Int
  get() = View.MeasureSpec.getMode(this)