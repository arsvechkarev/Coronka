package core.viewbuilding

import android.content.Context
import com.arsvechkarev.core.R
import viewdsl.dimen

object TextSizes {
  
  private val textSizes = FloatArray(6) { 0f }
  
  val H0: Float get() = textSizes[0]
  val H1: Float get() = textSizes[1]
  val H2: Float get() = textSizes[2]
  val H3: Float get() = textSizes[3]
  val H4: Float get() = textSizes[4]
  val H5: Float get() = textSizes[5]
  
  fun init(context: Context) {
    textSizes[0] = context.dimen(R.dimen.text_h0)
    textSizes[1] = context.dimen(R.dimen.text_h1)
    textSizes[2] = context.dimen(R.dimen.text_h2)
    textSizes[3] = context.dimen(R.dimen.text_h3)
    textSizes[4] = context.dimen(R.dimen.text_h4)
    textSizes[5] = context.dimen(R.dimen.text_h5)
  }
}