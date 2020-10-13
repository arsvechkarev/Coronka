package com.arsvechkarev.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
  
  override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
    return if (isEnabled) super.onInterceptTouchEvent(e) else false
  }
  
  override fun onTouchEvent(e: MotionEvent?): Boolean {
    return if (isEnabled) super.onTouchEvent(e) else false
  }
}