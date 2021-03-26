package base.resources

import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import base.R
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.paddingHorizontal
import com.arsvechkarev.viewdsl.paddingVertical
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
  
  val DrawerTextView: TextView.() -> Unit = {
    background(R.drawable.bg_drawer_item)
    textSize(TextSizes.H3)
    font(Fonts.SegoeUiBold)
    compoundDrawablePadding = 20.dp
    gravity(Gravity.CENTER or Gravity.START)
    textColor(Colors.TextPrimary)
    paddingHorizontal(16.dp)
    paddingVertical(8.dp)
  }
}