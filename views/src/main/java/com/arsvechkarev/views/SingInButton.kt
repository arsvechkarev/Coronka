package com.arsvechkarev.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.StateSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.paddingVertical
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.progressbar.ProgressBar
import core.viewbuilding.Colors
import core.viewbuilding.Colors.TextPrimary
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes

class SingInButton(context: Context) : FrameLayout(context) {
  
  private val textView get() = getChildAt(0)
  private val progressBar get() = getChildAt(1)
  
  init {
    addView(withViewBuilder {
      TextView(WrapContent, WrapContent) {
        gravity(Gravity.CENTER)
        font(Fonts.SegoeUiBold)
        textSize(TextSizes.H3)
        text(R.string.text_sign_in)
      }
    })
    addView(withViewBuilder {
      ProgressBar(context, TextPrimary, ProgressBar.Thickness.THICK).apply {
        size(32.dp, 32.dp)
        invisible()
      }
    })
    paddingVertical(6.dp)
    val stateListDrawable = StateListDrawable()
    val enabled = createEnabledDrawable(60.dp)
    val disabled = createDisabledDrawable(60.dp)
    stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), enabled)
    stateListDrawable.addState(StateSet.WILD_CARD, disabled)
    background(stateListDrawable)
    isClickable = true
    isFocusable = true
  }
  
  override fun onViewAdded(child: View) {
    child.layoutGravity(Gravity.CENTER)
  }
  
  fun showProgress() {
    textView.animateInvisible()
    progressBar.animateVisible()
  }
  
  fun hideProgress() {
    textView.animateVisible()
    progressBar.animateInvisible()
  }
  
  private fun createEnabledDrawable(radius: Int): Drawable {
    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BL_TR,
      intArrayOf(Colors.SignInButtonStart, Colors.SignInButtonEnd))
    val r = radius.toFloat()
    val outerRadii = floatArrayOf(r, r, r, r, r, r, r, r)
    gradientDrawable.cornerRadii = outerRadii
    val roundRectShape = RoundRectShape(outerRadii, null, null)
    val maskRect = ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = Colors.Ripple
    }
    val colorStateList = ColorStateList.valueOf(Colors.Ripple)
    return RippleDrawable(colorStateList, gradientDrawable, maskRect)
  }
  
  private fun createDisabledDrawable(radius: Int): Drawable {
    val r = radius.toFloat()
    val outerRadii = floatArrayOf(r, r, r, r, r, r, r, r)
    val roundRectShape = RoundRectShape(outerRadii, null, null)
    return ShapeDrawable().apply {
      shape = roundRectShape
      paint.color = Colors.Disabled
    }
  }
}