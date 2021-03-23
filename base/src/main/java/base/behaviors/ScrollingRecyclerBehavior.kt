package base.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import base.behaviors.HeaderBehavior.Companion.asHeader
import com.arsvechkarev.viewdsl.hasBehavior

class ScrollingRecyclerBehavior(context: Context? = null, attrs: AttributeSet? = null) :
  CoordinatorLayout.Behavior<RecyclerView>() {
  
  override fun layoutDependsOn(parent: CoordinatorLayout, child: RecyclerView, dependency: View): Boolean {
    return dependency.hasBehavior<HeaderBehavior>()
  }
  
  override fun onDependentViewChanged(parent: CoordinatorLayout, child: RecyclerView, dependency: View): Boolean {
    child.top = dependency.bottom
    return true
  }
  
  override fun onMeasureChild(
    parent: CoordinatorLayout,
    child: RecyclerView,
    parentWidthMeasureSpec: Int,
    widthUsed: Int,
    parentHeightMeasureSpec: Int,
    heightUsed: Int
  ): Boolean {
    val heightSize = MeasureSpec.getSize(parentHeightMeasureSpec)
    val header = findHeader(parent).asHeader
    val height = heightSize - header.minHeight
    val heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightSpec,
      heightUsed)
    return true
  }
  
  override fun onLayoutChild(parent: CoordinatorLayout, child: RecyclerView, layoutDirection: Int): Boolean {
    val top = findHeader(parent).bottom
    child.layout(0, top, parent.width, top + child.measuredHeight)
    return true
  }
  
  private fun findHeader(parent: CoordinatorLayout): View {
    repeat(parent.childCount) {
      val child = parent.getChildAt(it)
      if (child.hasBehavior<HeaderBehavior>()) {
        return child
      }
    }
    throw IllegalStateException()
  }
} 