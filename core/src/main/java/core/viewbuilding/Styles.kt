package core.viewbuilding

import android.text.TextUtils
import android.widget.TextView
import viewdsl.font
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
  
  val NewsTextView: TextView.() -> Unit = {
    apply(BaseTextView)
    ellipsize = TextUtils.TruncateAt.END
  }
}