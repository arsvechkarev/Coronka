package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/**
 * Standard [com.agoda.kakao.recycler.KRecyclerView] has problems with finding
 * view holders, therefore using custom implementation
 */
class KMyRecyclerView : KBaseView<KMyRecyclerView>, MyRecyclerViewAssertions {
  
  constructor(function: ViewBuilder.() -> Unit) : super(function)
  constructor(parent: Matcher<View>, function: ViewBuilder.() -> Unit) : super(parent, function)
  constructor(parent: DataInteraction, function: ViewBuilder.() -> Unit) : super(parent, function)
}

interface MyRecyclerViewAssertions : BaseAssertions {
  
  fun hasSize(size: Int) {
    view.matches<RecyclerView> { recyclerView -> recyclerView.adapter?.itemCount ?: 0 == size }
  }
  
  fun hasSizeLessThan(size: Int) {
    view.matches<RecyclerView> { recyclerView -> recyclerView.adapter?.itemCount ?: 0 < size }
  }
  
  fun hasItemViewAt(position: Int, matcher: (View) -> Boolean) {
    view.perform(recyclerScrollAction(position))
    view.matches<RecyclerView> { recyclerView ->
      val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
      matcher(viewHolder.itemView)
    }
  }
}

fun recyclerScrollAction(position: Int): ViewAction = object : ViewAction {
  
  override fun getConstraints(): Matcher<View> {
    return allOf(ViewMatchers.isAssignableFrom(RecyclerView::class.java), isDisplayed())
  }
  
  override fun getDescription(): String = "Scroll recycler view to position $position"
  
  override fun perform(uiController: UiController, view: View) {
    require(view is RecyclerView)
    view.scrollToPosition(position)
    uiController.loopMainThreadUntilIdle()
  }
}