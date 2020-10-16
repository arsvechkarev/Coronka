package core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import viewdsl.childWithTag

abstract class BaseFragment(
  private val layoutResId: Int = 0
) : Fragment(layoutResId) {
  
  private val viewsCache = HashMap<String, View>()
  
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return if (layoutResId == 0) {
      buildLayout() ?: super.onCreateView(inflater, container, savedInstanceState)
    } else
      super.onCreateView(inflater, container, savedInstanceState)
  }
  
  val drawerOpenCloseListener = object : HostActivity.DrawerOpenCloseListener {
    override fun onDrawerClosed() = this@BaseFragment.onDrawerClosed()
    override fun onDrawerOpened() = this@BaseFragment.onDrawerOpened()
  }
  
  open fun buildLayout(): View? {
    return null
  }
  
  open fun onAppearedOnScreen() = Unit
  
  open fun onNetworkAvailable() = Unit
  
  open fun onDrawerOpened() = Unit
  
  open fun onDrawerClosed() = Unit
  
  @Suppress("UNCHECKED_CAST")
  fun view(tag: String): View {
    if (viewsCache[tag] == null) {
      viewsCache[tag] = requireView().childWithTag(tag)
    }
    return viewsCache.getValue(tag)
  }
  
  fun imageView(tag: String): ImageView {
    return viewAs<ImageView>(tag)
  }
  
  fun textView(tag: String): TextView {
    return viewAs<TextView>(tag)
  }
  
  @Suppress("UNCHECKED_CAST")
  fun <T : View> viewAs(tag: String): T {
    if (viewsCache[tag] == null) {
      viewsCache[tag] = requireView().childWithTag(tag) as T
    }
    return viewsCache.getValue(tag) as T
  }
}