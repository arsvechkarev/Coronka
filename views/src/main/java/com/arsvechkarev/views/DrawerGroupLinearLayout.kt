package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class DrawerGroupLinearLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
  
  fun onTextViewClicked(textView: View) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.isSelected = false
    }
    textView.isSelected = true
  }
}