package core.viewbuilding

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.DimenRes
import core.extenstions.dimen
import core.viewbuilding.Size.Companion.WrapContent
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
  
  fun textView(
    width: Size = WrapContent,
    height: Size = WrapContent,
    style: TextView.() -> Unit = {}
  ): TextView {
    return TextView(context).apply(style).apply {
      layoutParams = context.createLayoutParams(width, height)
    }
  }
  
  fun <T : View> T.withSize(width: Size, height: Size): T {
    layoutParams = context.createLayoutParams(width, height)
    return this
  }
  
  fun View.paddingsRes(
    @DimenRes left: Int,
    @DimenRes top: Int,
    @DimenRes right: Int,
    @DimenRes bottom: Int
  ) {
    setPadding(
      context.dimen(left).toInt(),
      context.dimen(top).toInt(),
      context.dimen(right).toInt(),
      context.dimen(bottom).toInt()
    )
  }
  
  fun View.paddings(left: Int, top: Int, right: Int, bottom: Int) {
    setPadding(left, top, right, bottom)
  }
  
  fun dimen(dimenRes: Int) = context.dimen(dimenRes)
}