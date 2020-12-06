package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class DrawerGroupLinearLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
  
  fun setSelectedMenuItem(tag: String) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.isSelected = child.tag == tag
    }
  }
  
  fun onTextViewClicked(textView: View) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.isSelected = false
    }
    textView.isSelected = true
  }
}