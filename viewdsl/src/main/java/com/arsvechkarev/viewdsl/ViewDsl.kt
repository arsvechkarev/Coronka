package com.arsvechkarev.viewdsl

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout

fun View.heightWithMargins(): Int {
  val params = layoutParams as MarginLayoutParams
  return measuredHeight + params.topMargin + params.bottomMargin
}

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
  size(Size.IntSize(width), Size.IntSize(height), margins)
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

fun View.layoutGravity(gravity: Int) {
  when (parent as View) {
    is FrameLayout -> {
      (layoutParams as FrameLayout.LayoutParams).gravity = gravity
    }
    is LinearLayout -> {
      (layoutParams as LinearLayout.LayoutParams).gravity = gravity
    }
    is CoordinatorLayout -> {
      (layoutParams as CoordinatorLayout.LayoutParams).gravity = gravity
    }
    else -> throw IllegalStateException("Unable to set gravity to " +
        "parent ${this.parent as View}")
  }
}

fun View.margin(value: Int) {
  margins(value, value, value, value)
}

fun View.margins(
  start: Int = 0,
  top: Int = 0,
  end: Int = 0,
  bottom: Int = 0
) {
  if (layoutParams is MarginLayoutParams) {
    val params = layoutParams as MarginLayoutParams
    if (isLayoutLeftToRight) {
      params.setMargins(start, top, end, bottom)
    } else {
      params.setMargins(end, top, start, bottom)
    }
  } else {
    val params = MarginLayoutParams(layoutParams)
    if (isLayoutLeftToRight) {
      params.setMargins(start, top, end, bottom)
    } else {
      params.setMargins(end, top, start, bottom)
    }
    layoutParams = params
  }
}

fun View.padding(value: Int) {
  paddings(value, value, value, value)
}

fun View.paddingVertical(value: Int) {
  paddings(paddingStart, value, paddingEnd, value)
}

fun View.paddingHorizontal(value: Int) {
  paddings(value, paddingTop, value, paddingBottom)
}

fun View.paddingsRes(
  @DimenRes start: Int = 0,
  @DimenRes top: Int = 0,
  @DimenRes end: Int = 0,
  @DimenRes bottom: Int = 0
) {
  paddings(
    if (start == 0) 0 else dimen(start).toInt(),
    if (start == 0) 0 else dimen(top).toInt(),
    if (start == 0) 0 else dimen(end).toInt(),
    if (start == 0) 0 else dimen(bottom).toInt()
  )
}

fun View.paddings(
  start: Int = 0,
  top: Int = 0,
  end: Int = 0,
  bottom: Int = 0
) {
  if (isLayoutLeftToRight) {
    setPadding(start, top, end, bottom)
  } else {
    setPadding(end, top, start, bottom)
  }
}

fun onClick(vararg views: View, action: (View) -> Unit) {
  views.forEach { it.setOnClickListener(action) }
}

fun setClickable(isClickable: Boolean, vararg views: View) {
  views.forEach { it.isClickable = isClickable }
}

fun View.onClick(block: () -> Unit) {
  setOnClickListener { block() }
}

fun View.tag(tag: String) {
  this.tag = tag
}

fun View.id(idRes: Int) {
  id = idRes
}

fun View.background(drawable: Drawable) {
  background = drawable
}

fun View.backgroundGradient(orientation: GradientDrawable.Orientation, vararg colors: Int) {
  background = GradientDrawable(orientation, colors)
}

fun View.backgroundColor(@ColorInt color: Int) {
  setBackgroundColor(color)
}

fun View.background(@DrawableRes drawableRes: Int) {
  background = context.getDrawable(drawableRes)
}

fun View.behavior(behavior: CoordinatorLayout.Behavior<*>) {
  (layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.getBehavior(): T {
  return (layoutParams as CoordinatorLayout.LayoutParams).behavior as T
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.hasBehavior(): Boolean {
  return (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? T != null
}

fun View.childView(tag: String): View {
  return findViewWithTag(tag)
}

fun View.childTextView(tag: String): TextView {
  return findViewWithTag(tag) as TextView
}

@Suppress("UNCHECKED_CAST")
fun <T : View> T.childViewAs(tag: String): T {
  return childView(tag) as T
}