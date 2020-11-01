package com.arsvechkarev.viewdsl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.Size.IntSize

fun View.layoutWithLeftTop(left: Int, top: Int) {
  layout(left, top, left + measuredWidth, top + measuredHeight)
}

inline fun <reified T : View> Context.withViewBuilder(builder: ViewBuilder.() -> T): T {
  val viewBuilder = ViewBuilder(this)
  return builder(viewBuilder)
}

inline fun <reified T : View> Fragment.withViewBuilder(builder: ViewBuilder.() -> T): T {
  return requireContext().withViewBuilder(builder)
}

inline fun <reified T : View> View.withViewBuilder(builder: ViewBuilder.() -> T): T {
  return context.withViewBuilder(builder)
}

inline fun <reified T : View> Fragment.view(
  width: Size = WrapContent,
  height: Size = WrapContent,
  crossinline style: T.() -> Unit = {},
  crossinline block: T.() -> Unit = {}
): T {
  val builder = ViewBuilder(requireContext())
  return builder.view<T>(width, height, style, block)
}

class ViewBuilder(val context: Context) {
  
  val StatusBarHeight get() = context.statusBarHeight
  
  fun Any.TextView(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: TextView.() -> Unit = {},
    block: TextView.() -> Unit = {}
  ) = TextView(context).size(width, height).apply(style).apply(block)
  
  fun Any.LinearLayout(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: LinearLayout.() -> Unit = {},
    block: LinearLayout.() -> Unit = {}
  ) = LinearLayout(context).size(width, height).apply(style).apply(block)
  
  fun Any.FrameLayout(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: FrameLayout.() -> Unit = {},
    block: FrameLayout.() -> Unit = {}
  ) = FrameLayout(context).size(width, height).apply(style).apply(block)
  
  fun Any.CoordinatorLayout(
    width: Size,
    height: Size,
    style: CoordinatorLayout.() -> Unit = {},
    block: CoordinatorLayout.() -> Unit = {}
  ) = CoordinatorLayout(context).size(width, height).apply(style).apply(block)
  
  inline fun <reified T : View> view(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit = {},
  ): T {
    val viewGroupParams = ViewGroup.LayoutParams(
      context.determineSize(width),
      context.determineSize(height)
    )
    val viewConstrictor = T::class.java.getDeclaredConstructor(Context::class.java)
    val instance = viewConstrictor.newInstance(context)
    instance.layoutParams = viewGroupParams
    return instance.apply(style).apply(block)
  }
  
  inline fun <reified T : View> FrameLayout.child(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, FrameLayout.LayoutParams>(width, height, style, block)
  
  inline fun <reified T : View> FrameLayout.child(
    width: Int,
    height: Int,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, FrameLayout.LayoutParams>(IntSize(width), IntSize(height), style, block)
  
  inline fun <reified T : View> LinearLayout.child(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit
  ) = child<T, LinearLayout.LayoutParams>(width, height, style, block)
  
  inline fun <reified T : View> LinearLayout.child(
    width: Int,
    height: Int,
    style: T.() -> Unit = {},
    block: T.() -> Unit
  ) = child<T, LinearLayout.LayoutParams>(IntSize(width), IntSize(height), style, block)
  
  inline fun <reified T : View> CoordinatorLayout.child(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, CoordinatorLayout.LayoutParams>(width, height, style, block)
  
  inline fun <reified T : View> CoordinatorLayout.child(
    width: Int,
    height: Int,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, CoordinatorLayout.LayoutParams>(IntSize(width), IntSize(height), style, block)
  
  inline fun <reified T : View> ConstraintLayout.child(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, ConstraintLayout.LayoutParams>(width, height, style, block)
  
  inline fun <reified T : View> ConstraintLayout.child(
    width: Int,
    height: Int,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ) = child<T, ConstraintLayout.LayoutParams>(IntSize(width), IntSize(height), style, block)
  
  inline fun <reified T : View, reified P : ViewGroup.LayoutParams> ViewGroup.child(
    width: Size,
    height: Size,
    style: T.() -> Unit = {},
    block: T.() -> Unit
  ): T {
    val viewGroupParams = ViewGroup.LayoutParams(
      context.determineSize(width),
      context.determineSize(height)
    )
    val viewConstrictor = T::class.java.getDeclaredConstructor(Context::class.java)
    val paramsConstructor = P::class.java.getDeclaredConstructor(ViewGroup.LayoutParams::class.java)
    val child = viewConstrictor.newInstance(context)
    val params = paramsConstructor.newInstance(viewGroupParams)
    addView(child, params)
    return child.apply(style).apply(block)
  }
}