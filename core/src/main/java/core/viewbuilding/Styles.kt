package core.viewbuilding

import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import viewdsl.Ints.dp
import viewdsl.font
import viewdsl.gravity
import viewdsl.paddingHorizontal
import viewdsl.paddingVertical
import viewdsl.textColor
import viewdsl.textSize

object Styles {
  
  val BaseTextView: TextView.() -> Unit = {
    textSize(TextSizes.H5)
    textColor(Colors.TextPrimary)
    font(Fonts.SegoeUi)
  }
  
  val BoldTextView: TextView.() -> Unit = {
    apply(BaseTextView)
    font(Fonts.SegoeUiBold)
  }
  
  val RetryTextView: TextView.() -> Unit = {
    apply(BoldTextView)
    font(Fonts.SegoeUiBold)
    paddingVertical(8.dp)
    paddingHorizontal(12.dp)
    textColor(Colors.Failure)
  }
  
  val HeaderTextView: TextView.() -> Unit = {
    apply(BoldTextView)
    textSize(TextSizes.Header)
    gravity(Gravity.CENTER)
  }
  
  val NewsTextView: TextView.() -> Unit = {
    apply(BaseTextView)
    ellipsize = TextUtils.TruncateAt.END
  }
}