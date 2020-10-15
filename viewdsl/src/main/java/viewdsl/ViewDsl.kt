package viewdsl

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.coordinatorlayout.widget.CoordinatorLayout

fun View.visible() {
  visibility = View.VISIBLE
}

fun View.invisible() {
  visibility = View.INVISIBLE
}

fun View.gone() {
  visibility = View.GONE
}

fun <T : View> T.size(
  width: Int,
  height: Int,
  margins: Margins = Margins()
): T {
  size(Size.IntValue(width), Size.IntValue(height), margins)
  return this
}

fun <T : View> T.size(
  width: Size,
  height: Size,
  margins: Margins = Margins()
): T {
  if (layoutParams == null) {
    layoutParams = context.createLayoutParams(width, height, margins)
  } else {
    layoutParams.width = context.determineSize(width)
    layoutParams.height = context.determineSize(height)
  }
  return this
}

fun View.margins(
  left: Int = 0,
  top: Int = 0,
  right: Int = 0,
  bottom: Int = 0
) {
  if (layoutParams is MarginLayoutParams) {
    (layoutParams as MarginLayoutParams).setMargins(left, top, right, bottom)
  } else {
    val params = MarginLayoutParams(layoutParams)
    params.setMargins(left, top, right, bottom)
    layoutParams = params
  }
}

fun View.layoutGravity(gravity: Int) {
  when (parent as View) {
    is FrameLayout -> {
      val params = FrameLayout.LayoutParams(layoutParams)
      params.gravity = gravity
      layoutParams = params
    }
    is LinearLayout -> {
      val params = LinearLayout.LayoutParams(layoutParams)
      params.gravity = gravity
      layoutParams = params
    }
    else -> throw IllegalStateException("Unable to set gravity to " +
        "parent ${this.parent as View}")
  }
}

fun View.paddingsRes(
  @DimenRes left: Int,
  @DimenRes top: Int,
  @DimenRes right: Int,
  @DimenRes bottom: Int
) {
  setPadding(
    context.resources.getDimension(left).toInt(),
    context.resources.getDimension(top).toInt(),
    context.resources.getDimension(right).toInt(),
    context.resources.getDimension(bottom).toInt()
  )
}

fun View.padding(value: Int) {
  setPadding(value, value, value, value)
}

fun View.paddingVertical(value: Int) {
  setPadding(paddingLeft, value, paddingRight, value)
}

fun View.paddingHorizontal(value: Int) {
  setPadding(value, paddingTop, value, paddingBottom)
}

fun View.paddings(
  left: Int = 0,
  top: Int = 0,
  right: Int = 0,
  bottom: Int = 0
) {
  setPadding(left, top, right, bottom)
}

fun onClick(vararg views: View, action: (View) -> Unit) {
  views.forEach { it.setOnClickListener(action) }
}

fun View.onClick(block: () -> Unit) {
  setOnClickListener { block() }
}

fun View.tag(tag: String) {
  this.tag = tag
}

fun <T : View> T.childWithTag(tag: String): T {
  return findViewWithTag(tag)
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.getBehavior(): T {
  return (layoutParams as CoordinatorLayout.LayoutParams).behavior as T
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.hasBehavior(): Boolean {
  return (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? T != null
}
