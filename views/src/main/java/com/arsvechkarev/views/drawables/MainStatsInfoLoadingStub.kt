package com.arsvechkarev.views.drawables

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Canvas
import android.graphics.Path
import com.arsvechkarev.viewdsl.dimen
import com.arsvechkarev.views.R
import com.arsvechkarev.views.generalstatsviews.MainGeneralStatsView
import core.extenstions.execute
import core.extenstions.i

class MainStatsInfoLoadingStub(
  private var context: Context?
) : BaseLoadingStub() {
  
  private val cornersRadius = dimen(R.dimen.bg_overlay_corners_small)
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    val context = context ?: return
    var adjWidth = width
    if (context.resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
      val margin = context.resources.getDimension(R.dimen.general_stats_view_landscape_margin)
      adjWidth -= margin * 2
    }
    val margin = MainGeneralStatsView.getItemMargin(adjWidth.i)
    val rectWidth = (adjWidth - margin * 2) / 3
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
  
  override fun draw(canvas: Canvas) {
    val context = context ?: return
    canvas.execute {
      if (context.resources.configuration.orientation == ORIENTATION_LANDSCAPE) {
        val margin = context.resources.getDimension(R.dimen.general_stats_view_landscape_margin)
        canvas.translate(margin, 0f)
      }
      super.draw(canvas)
    }
  }
  
  fun release() {
    context = null
  }
}