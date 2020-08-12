package core

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.arsvechkarev.core.R
import core.concurrency.AndroidThreader
import java.util.concurrent.CountDownLatch

object FontManager {
  
  private val initializationLatch = CountDownLatch(1)
  
  var rubik: Typeface? = null
    get() {
      initializationLatch.await()
      return field!!
    }
  
  var segoeUI: Typeface? = null
    get() {
      initializationLatch.await()
      return field!!
    }
  
  fun init(context: Context) {
    AndroidThreader.onBackground {
      rubik = Typeface.createFromAsset(context.assets, "rubik_medium.ttf")
      segoeUI = ResourcesCompat.getFont(context, R.font.segoe_ui_bold)
      initializationLatch.countDown()
    }
  }
}