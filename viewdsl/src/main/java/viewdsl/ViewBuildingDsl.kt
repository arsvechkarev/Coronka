package viewdsl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import viewdsl.Size.Companion.WrapContent
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun View.layoutWithLeftTop(left: Int, top: Int) {
  layout(left, top, left + measuredWidth, top + measuredHeight)
}

@ExperimentalContracts
fun Context.buildViews(builder: (ViewBuilder) -> Unit) {
  contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
  ViewBuilder(this).apply(builder)
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
  
  inline fun <reified T : View> LinearLayout.child(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: T.() -> Unit = {},
    block: T.() -> Unit
  ): T {
    return child<T, LinearLayout.LayoutParams>(width, height, style, block)
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
  
  fun dimen(dimenRes: Int) = context.resources.getDimension(dimenRes)
}