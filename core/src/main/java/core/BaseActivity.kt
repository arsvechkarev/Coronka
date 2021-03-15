package core

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.arsvechkarev.viewdsl.childView

abstract class BaseActivity : AppCompatActivity() {
  
  private val viewsCache = HashMap<String, View>()
  
  @Suppress("UNCHECKED_CAST")
  fun view(tag: String): View {
    if (viewsCache[tag] == null) {
      viewsCache[tag] = window.decorView.childView(tag)
    }
    return viewsCache.getValue(tag)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun <T : View> viewAs(tag: String) = view(tag) as T
}