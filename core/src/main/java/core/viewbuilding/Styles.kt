package core.viewbuilding

import android.text.TextUtils
import android.widget.TextView
import core.extenstions.textSize

object Styles {
  
  val BaseTextView: TextView.() -> Unit = {
    textSize(TextSizes.H5)
    setTextColor(Colors.TextPrimary)
    typeface = Fonts.SegoeUi
  }
  
  val NewsTextView: TextView.() -> Unit = {
    apply(BaseTextView)
    ellipsize = TextUtils.TruncateAt.END
  }
}