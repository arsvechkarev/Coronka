package core.viewbuilding

import com.arsvechkarev.core.R
import com.arsvechkarev.viewdsl.dimen

object TextSizes {
  
  private val textSizes = FloatArray(8) { 0f }
  
  val H0: Float get() = textSizes[0]
  val H1: Float get() = textSizes[1]
  val H2: Float get() = textSizes[2]
  val H3: Float get() = textSizes[3]
  val H4: Float get() = textSizes[4]
  val H5: Float get() = textSizes[5]
  val Header: Float get() = textSizes[6]
  val MainTitle: Float get() = textSizes[7]
  
  init {
    textSizes[0] = dimen(R.dimen.text_h0)
    textSizes[1] = dimen(R.dimen.text_h1)
    textSizes[2] = dimen(R.dimen.text_h2)
    textSizes[3] = dimen(R.dimen.text_h3)
    textSizes[4] = dimen(R.dimen.text_h4)
    textSizes[5] = dimen(R.dimen.text_h5)
    textSizes[6] = dimen(R.dimen.text_header)
    textSizes[7] = dimen(R.dimen.text_main_title)
  }
}