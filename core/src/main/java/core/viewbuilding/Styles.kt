package core.viewbuilding

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.text.TextUtils
import android.view.Gravity
import android.widget.TextView
import com.arsvechkarev.core.R
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
  
  val ClickableTextView: TextView.() -> Unit = {
    apply(BoldTextView)
    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(
      Colors.SignInButtonStart, Colors.SignInButtonEnd))
    val r = 60.dp.toFloat()
    val outerRadii = floatArrayOf(r, r, r, r, r, r, r, r)
    gradientDrawable.cornerRadii = outerRadii
    val roundRectShape = RoundRectShape(outerRadii, null, null)
    val maskRect = ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = Colors.Ripple
    }
    val colorStateList = ColorStateList.valueOf(Colors.Ripple)
    background(RippleDrawable(colorStateList, gradientDrawable, maskRect))
    paddingVertical(8.dp)
    paddingHorizontal(24.dp)
    textSize(TextSizes.H3)
    isClickable = true
    isFocusable = true
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