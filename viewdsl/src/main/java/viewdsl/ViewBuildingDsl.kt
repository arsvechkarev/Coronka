package viewdsl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import viewdsl.Size.Companion.WrapContent

fun View.layoutWithLeftTop(left: Int, top: Int) {
  layout(left, top, left + measuredWidth, top + measuredHeight)
}

fun Context.buildViews(builder: (ViewBuilder) -> Unit) {
  ViewBuilder(this).apply(builder)
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
  
  fun Any.ImageView(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: ImageView.() -> Unit = {},
    block: ImageView.() -> Unit = {}
  ) = ImageView(context).size(width, height).apply(style).apply(block)
  
  inline fun <reified T : View> LinearLayout.child(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: T.() -> Unit = {},
    block: T.() -> Unit
  ): T {
    return child<T, LinearLayout.LayoutParams>(width, height, style, block)
  }
  
  inline fun <reified T : View> view(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
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
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: T.() -> Unit = {},
    block: T.() -> Unit,
  ): T {
    return child<T, FrameLayout.LayoutParams>(width, height, style, block)
  }
  
  inline fun <reified T : View, reified P : ViewGroup.LayoutParams> ViewGroup.child(
    width: Size = WrapContent,
    height: Size = WrapContent,
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