package com.arsvechkarev.tips.presentation

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.tips.R
import com.arsvechkarev.views.behaviors.BottomSheetBehavior
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import core.BaseFragment
import core.hostActivity
import core.recycler.StaticDelegateBuilder
import core.recycler.adapter
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheet
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheetCross
import kotlinx.android.synthetic.main.fragment_tips.tipsRecyclerView
import kotlinx.android.synthetic.main.fragment_tips.tipsTextAnswer
import kotlinx.android.synthetic.main.fragment_tips.tipsTextTitle
import kotlinx.android.synthetic.main.item_faq.view.tipsItemFaq
import kotlinx.android.synthetic.main.item_header.view.tipsTextHeader
import kotlinx.android.synthetic.main.item_main_header.view.tipsGradientHeaderView
import kotlinx.android.synthetic.main.item_main_header.view.tipsImageDrawer
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionImage
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionTitle
import viewdsl.background

class TipsFragment : BaseFragment(R.layout.fragment_tips) {
  
  override fun onInit() {
    val behavior = (tipsBottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
    val bottomSheetBehavior = behavior as BottomSheetBehavior<View>
    behavior.onHide = { hostActivity.enableTouchesOnDrawer() }
    behavior.onShow = { hostActivity.disableTouchesOnDrawer() }
    tipsBottomSheetCross.setOnClickListener { bottomSheetBehavior.hide() }
    val adapter = adapter {
      delegate(MainHeader::class, mainHeaderLayout())
      delegate(Header::class, headerLayout(getString(R.string.text_faq)))
      delegate(FAQItem::class, faqItemLayout(behavior))
      delegate(Header::class, headerLayout(getString(R.string.text_symptoms)))
      delegate(SymptomsLayout::class, symptomsLayout())
      delegate(Header::class, headerLayout(getString(R.string.text_prevention_tips)))
      delegate(PreventionItem::class, preventionsLayout())
    }
    tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    tipsRecyclerView.adapter = adapter
    tipsRecyclerView.setHasFixedSize(true)
  }
  
  override fun onDrawerOpened() {
    tipsRecyclerView.isEnabled = false
  }
  
  override fun onDrawerClosed() {
    tipsRecyclerView.isEnabled = true
  }
  
  private fun mainHeaderLayout(): StaticDelegateBuilder<MainHeader>.() -> Unit = {
    data(MainHeader)
    layoutRes(R.layout.item_main_header)
    onInitViewHolder {
      itemView.tipsImageDrawer.setOnClickListener { hostActivity.openDrawer() }
      itemView.tipsGradientHeaderView.background(GradientHeaderDrawable())
    }
  }
  
  private fun headerLayout(title: String): StaticDelegateBuilder<Header>.() -> Unit = {
    data(Header(title))
    layoutRes(R.layout.item_header)
    onBind { view, item ->
      view.tipsTextHeader.text = item.title
    }
  }
  
  private fun symptomsLayout(): StaticDelegateBuilder<SymptomsLayout>.() -> Unit = {
    data(SymptomsLayout)
    layoutRes(R.layout.item_symptoms)
  }
  
  private fun faqItemLayout(behavior: BottomSheetBehavior<*>): StaticDelegateBuilder<FAQItem>.() -> Unit = {
    data(listOf(
      FAQItem(R.string.q1, R.string.a1),
      FAQItem(R.string.q2, R.string.a2),
      FAQItem(R.string.q3, R.string.a3),
      FAQItem(R.string.q4, R.string.a4),
      FAQItem(R.string.q5, R.string.a5)
    ))
    layoutRes(R.layout.item_faq)
    onInitViewHolder {
      itemView.setOnClickListener {
        if (tipsRecyclerView.isEnabled) {
          tipsTextTitle.text = getString(item.questionLayoutRes)
          tipsTextAnswer.text = getString(item.answerLayoutRes)
          behavior.show()
        }
      }
    }
    onBind { view, item ->
      view.tipsItemFaq.text = getString(item.questionLayoutRes)
    }
  }
  
  private fun preventionsLayout(): StaticDelegateBuilder<PreventionItem>.() -> Unit = {
    data(listOf(
      PreventionItem(R.drawable.image_no_touch, getString(R.string.text_prevention_no_touch)),
      PreventionItem(R.drawable.image_protection, getString(R.string.text_prevention_protection)),
      PreventionItem(R.drawable.image_wash_hands, getString(R.string.text_prevention_wash_hands)),
      PreventionItem(R.drawable.image_social_distancing,
        getString(R.string.text_prevention_social_distancing))
    ))
    layoutRes(R.layout.item_prevention)
    onBind { view, item ->
      view.tipsItemPreventionImage.setImageResource(item.imageRes)
      view.tipsItemPreventionTitle.text = item.title
    }
  }
}