package progressbar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ProgressBar

class ComplexProgressBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  
  init {
    if (Build.VERSION.SDK_INT > 21) {
      addView(VectorDrawableProgress(context), LayoutParams(MATCH_PARENT, MATCH_PARENT))
    } else {
      addView(ProgressBar(context), LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }
  }
}