package com.arsvechkarev.tips.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.tips.R
import com.arsvechkarev.views.behaviors.BottomSheetBehavior
import core.recycler.AdapterDelegateBuilder
import core.recycler.createAdapter
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheet
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheetCross
import kotlinx.android.synthetic.main.fragment_tips.tipsRecyclerView
import kotlinx.android.synthetic.main.fragment_tips.tipsTextAnswer
import kotlinx.android.synthetic.main.fragment_tips.tipsTextTitle
import kotlinx.android.synthetic.main.item_faq.view.tipsItemFaq
import kotlinx.android.synthetic.main.item_header.view.tipsTextHeader
import kotlinx.android.synthetic.main.item_main_header.view.tipsImageDrawer
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionImage
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionTitle

class TipsFragment : Fragment(R.layout.fragment_tips) {
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val behavior = (tipsBottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
    val bottomSheetBehavior = behavior as BottomSheetBehavior<View>
    tipsBottomSheetCross.setOnClickListener { bottomSheetBehavior.hide() }
    val adapter = createAdapter {
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
  }
  
  private fun mainHeaderLayout(): AdapterDelegateBuilder<MainHeader>.() -> Unit {
    return {
      data(MainHeader)
      layoutRes(R.layout.item_main_header)
      onViewHolderInitialization { header, _ ->
        header.itemView.tipsImageDrawer.setOnClickListener {
          Toast.makeText(requireContext(), "Lol", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
  
  private fun headerLayout(title: String): AdapterDelegateBuilder<Header>.() -> Unit = {
    data(Header(title))
    layoutRes(R.layout.item_header)
    onBindViewHolder { view, item ->
      view.tipsTextHeader.text = item.title
    }
  }
  
  private fun symptomsLayout(): AdapterDelegateBuilder<SymptomsLayout>.() -> Unit = {
    data(SymptomsLayout)
    layoutRes(R.layout.item_symptoms)
  }
  
  private fun faqItemLayout(behavior: BottomSheetBehavior<*>): AdapterDelegateBuilder<FAQItem>.() -> Unit = {
    data(listOf(
      FAQItem(R.string.q1, R.string.a1),
      FAQItem(R.string.q2, R.string.a2),
      FAQItem(R.string.q3, R.string.a3),
      FAQItem(R.string.q4, R.string.a4),
      FAQItem(R.string.q5, R.string.a5)
    ))
    layoutRes(R.layout.item_faq)
    onViewHolderInitialization { header, data ->
      header.itemView.setOnClickListener {
        val item = data[header.adapterPosition - 2 /* 2 elements before faq item*/]
        tipsTextTitle.text = getString(item.questionLayoutRes)
        tipsTextAnswer.text = getString(item.answerLayoutRes)
        behavior.show()
      }
    }
    onBindViewHolder { view, faqItem ->
      view.tipsItemFaq.text = getString(faqItem.questionLayoutRes)
    }
  }
  
  private fun preventionsLayout(): AdapterDelegateBuilder<PreventionItem>.() -> Unit = {
    data(listOf(
      PreventionItem(R.drawable.image_no_touch, getString(R.string.text_prevention_no_touch)),
      PreventionItem(R.drawable.image_protection, getString(R.string.text_prevention_protection)),
      PreventionItem(R.drawable.image_wash_hands, getString(R.string.text_prevention_wash_hands)),
      PreventionItem(R.drawable.image_social_distancing,
        getString(R.string.text_prevention_social_distancing))
    ))
    layoutRes(R.layout.item_prevention)
    onBindViewHolder { view, preventionItem ->
      view.tipsItemPreventionImage.setImageResource(preventionItem.imageRes)
      view.tipsItemPreventionTitle.text = preventionItem.title
    }
  }
}