package base.drawables

import android.content.Context
import android.graphics.Path
import base.R

class StatsGraphLoadingStub(context: Context) : BaseLoadingStub() {
  
  private val cornersRadius = context.resources.getDimension(
    R.dimen.bg_overlay_corners_small)
  
  override fun drawBackgroundWithPath(path: Path, width: Float, height: Float) {
    path.addRoundRect(0f, 0f, width, height, cornersRadius, cornersRadius,
      Path.Direction.CW)
  }
}