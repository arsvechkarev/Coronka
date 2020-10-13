package core.extenstions

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout

operator fun View.contains(ev: MotionEvent): Boolean {
  val x = ev.x
  val y = ev.y
  return x >= left && y >= top && x <= right && y <= bottom
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

fun View.heightWithMargins(): Int {
  val params = layoutParams as ViewGroup.MarginLayoutParams
  return height + params.topMargin + params.bottomMargin
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.getBehavior(): T {
  return (layoutParams as CoordinatorLayout.LayoutParams).behavior as T
}

inline fun <reified T : CoordinatorLayout.Behavior<*>> View.hasBehavior(): Boolean {
  return (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior as? T != null
}

fun ViewGroup.addViews(vararg views: View) {
  views.forEach { addView(it) }
}

inline fun ViewGroup.forEachChild(action: (child: View) -> Unit) {
  for (i in 0 until childCount) action(getChildAt(i))
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View {
  return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun ViewGroup.animateChildrenVisible() = forEachChild { it.animateVisible() }

fun ViewGroup.animateChildrenInvisible() = forEachChild { it.animateInvisible() }

fun onClick(vararg views: View, action: (View) -> Unit) {
  views.forEach { it.setOnClickListener(action) }
}

fun <T : TextView> T.textSize(size: Float): T {
  setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
  return this
}