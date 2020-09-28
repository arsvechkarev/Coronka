package com.arsvechkarev.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import core.extenstions.f

class BoxRecyclerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
  
  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
    canvas.drawRect(0f, 0f, width.f, height.f, Paint().apply {
      color = Color.RED
      style = Paint.Style.STROKE
      strokeWidth = 24f
    })
  }
}