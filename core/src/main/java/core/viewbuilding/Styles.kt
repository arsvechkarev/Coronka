package core.viewbuilding

import android.widget.TextView
import core.extenstions.textSize

object Styles {
  
  val BaseTextView: TextView.() -> Unit
    get() = {
      textSize(TextSizes.H5)
      setTextColor(Colors.TextPrimary)
    }
}