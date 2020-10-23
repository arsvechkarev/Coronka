package core.viewbuilding

import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.textColor
import com.arsvechkarev.viewdsl.textSize

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