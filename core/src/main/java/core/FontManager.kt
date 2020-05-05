package core

import android.content.Context
import android.graphics.Typeface
import core.concurrency.AndroidThreader
import java.util.concurrent.CountDownLatch

object FontManager {
  
  private val initializationLatch = CountDownLatch(1)
  
  var rubik: Typeface? = null
    get() {
      initializationLatch.await()
      return field!!
    }
  
  fun init(context: Context) {
    AndroidThreader.onBackground {
      rubik = Typeface.createFromAsset(context.assets, "rubik_medium.ttf")
      initializationLatch.countDown()
    }
  }
}