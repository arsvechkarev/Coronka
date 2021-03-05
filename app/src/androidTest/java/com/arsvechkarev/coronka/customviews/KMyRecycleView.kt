package com.arsvechkarev.coronka.customviews

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.DataInteraction
import com.agoda.kakao.common.assertions.BaseAssertions
import com.agoda.kakao.common.builders.ViewBuilder
import com.agoda.kakao.common.views.KBaseView
import com.arsvechkarev.coronka.matches
import org.hamcrest.Matcher

class KMyRecycleView : KBaseView<KMyRecycleView>, MyRecyclerViewAssertions {
  
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
    view.matches<RecyclerView> { recyclerView ->
      val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)!!
      matcher(viewHolder.itemView)
    }
  }
}