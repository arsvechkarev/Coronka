package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import core.FontManager

class CustomTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : TextView(context, attrs) {
  
  init {
    typeface = FontManager.rubik
  }
  
}