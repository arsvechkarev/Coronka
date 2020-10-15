package com.arsvechkarev.views.drawables

import android.content.Context
import android.graphics.Path
import com.arsvechkarev.views.GeneralStatsView
import com.arsvechkarev.views.R
import core.extenstions.i
import viewdsl.dimen

class MainStatsInfoLoadingStub(context: Context) : BaseLoadingStub() {
  
  private val cornersRadius = context.dimen(R.dimen.bg_overlay_corners_small)
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val margin = GeneralStatsView.getSquareMargin(width.i)
    val rectWidth = (width - margin * 2) / 3
    path.addRoundRect(
      0f, 0f, rectWidth, height,
      cornersRadius, cornersRadius, Path.Direction.CW
    )
    path.addRoundRect(
      rectWidth + margin,
      0f,
      rectWidth * 2 + margin,
      height,
      cornersRadius, cornersRadius, Path.Direction.CW
    )
    path.addRoundRect(
      rectWidth * 2 + margin * 2,
      0f,
      rectWidth * 3 + margin * 2,
      height,
      cornersRadius, cornersRadius, Path.Direction.CW
    )
  }
}