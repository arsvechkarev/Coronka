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
import androidx.appcompat.widget.AppCompatTextView
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes
import viewdsl.Ints.dp
import viewdsl.background
import viewdsl.font
import viewdsl.gravity
import viewdsl.paddingVertical
import viewdsl.textSize

class RegistrationButton(context: Context) : AppCompatTextView(context) {
  
  init {
    paddingVertical(12.dp)
    gravity(Gravity.CENTER)
    font(Fonts.SegoeUiBold)
    textSize(TextSizes.H3)
    val stateListDrawable = StateListDrawable()
    val enabled = createEnabledDrawable(60.dp)
    val disabled = createDisabledDrawable(60.dp)
    stateListDrawable.addState(intArrayOf(android.R.attr.state_enabled), enabled)
    stateListDrawable.addState(StateSet.WILD_CARD, disabled)
    background(stateListDrawable)
    isClickable = true
    isFocusable = true
  }
  
  private fun createEnabledDrawable(radius: Int): Drawable {
    val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BL_TR,
      intArrayOf(Colors.Accent, Colors.AccentDarker))
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