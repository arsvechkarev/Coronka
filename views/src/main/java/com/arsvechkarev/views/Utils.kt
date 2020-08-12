package com.arsvechkarev.views

import android.text.BoringLayout
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils

fun boringLayoutOf(
  textPaint: TextPaint,
  text: CharSequence,
  maxWidth: Float = -1f,
  alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
): BoringLayout {
  val metrics = BoringLayout.isBoring(text, textPaint)
  if (maxWidth == -1f) {
    return BoringLayout.make(text, textPaint, metrics.width,
      alignment, 0f, 0f, metrics, false)
  } else {
    return BoringLayout.make(text, textPaint, metrics.width,
      alignment, 0f, 0f, metrics, false,
      TextUtils.TruncateAt.END, maxWidth.toInt())
  }
}