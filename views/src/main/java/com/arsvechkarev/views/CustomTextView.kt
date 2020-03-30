package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import core.FontManager

class CustomTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
  
  init {
    typeface = FontManager.rubik
  }
  
}