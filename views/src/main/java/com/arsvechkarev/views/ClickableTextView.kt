package com.arsvechkarev.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import core.FontManager
import core.extenstions.dpInt
import core.extenstions.getAttrColor

class ClickableTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  
  init {
    isClickable = true
    isFocusable = true
    typeface = FontManager.rubik
    setPadding(12.dpInt, 4.dpInt, 12.dpInt, 4.dpInt)
    setTypeface(typeface, Typeface.BOLD)
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_h4))
    setTextColor(context.getAttrColor(R.attr.colorTextAccent))
    setBackgroundResource(R.drawable.bg_clickable_text_view)
  }
}