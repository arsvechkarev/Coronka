package com.arsvechkarev.tips.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.common.BottomSheetBehavior
import com.arsvechkarev.tips.R
import core.recycler.AdapterDelegateBuilder
import core.recycler.DisplayableItem
import core.recycler.createAdapter
import kotlinx.android.synthetic.main.fragment_tips.recyclerView
import kotlinx.android.synthetic.main.fragment_tips.textAnswer
import kotlinx.android.synthetic.main.fragment_tips.textTitle
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheet
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheetCross
import kotlinx.android.synthetic.main.item_faq.view.tipsItemFaq
import kotlinx.android.synthetic.main.item_header.view.tipsTextHeader
import kotlinx.android.synthetic.main.item_main_header.view.tipsImageDrawer
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionImage
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionText
import kotlinx.android.synthetic.main.item_prevention.view.tipsItemPreventionTitle

class TipsFragment : Fragment(R.layout.fragment_tips) {
  
  object MainHeader : DisplayableItem
  
  class Header(val title: String) : DisplayableItem
  
  class FAQItem(
    val questionLayoutRes: Int,
    val answerLayoutRes: Int
  ) : DisplayableItem
  
  object SymptomsLayout : DisplayableItem
  
  class PreventionItem(
    val imageRes: Int,
    val title: String,
    val text: String
  ) : DisplayableItem
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val behavior = (tipsBottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
    val bottomSheetBehavior = behavior as BottomSheetBehavior<View>
    tipsBottomSheetCross.setOnClickListener { bottomSheetBehavior.hide() }
    val adapter = createAdapter {
      delegate(MainHeader::class, mainHeaderLayout())
      delegate(Header::class, headerLayout("FAQ"))
      delegate(FAQItem::class, faqItemLayout(behavior))
      delegate(Header::class, headerLayout("Symptoms"))
      delegate(SymptomsLayout::class, symptomsLayout())
      delegate(Header::class, headerLayout("Prevention tips"))
      delegate(PreventionItem::class, preventionsLayout())
    }
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter
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
        textTitle.text = getString(item.questionLayoutRes)
        textAnswer.text = getString(item.answerLayoutRes)
        behavior.show()
      }
    }
    onBindViewHolder { view, faqItem ->
      view.tipsItemFaq.text = getString(faqItem.questionLayoutRes)
    }
  }
  
  private fun preventionsLayout(): AdapterDelegateBuilder<PreventionItem>.() -> Unit = {
    data(listOf(
      PreventionItem(R.drawable.image_wash_hands, "wert", "qwer"),
      PreventionItem(R.drawable.image_wash_hands, "wert", "qwer"),
      PreventionItem(R.drawable.image_wash_hands, "wert", "qwer"),
      PreventionItem(R.drawable.image_wash_hands, "wert", "qwer")
    ))
    layoutRes(R.layout.item_prevention)
    onBindViewHolder { view, preventionItem ->
      view.tipsItemPreventionImage.setImageResource(preventionItem.imageRes)
      view.tipsItemPreventionTitle.text = preventionItem.title
      view.tipsItemPreventionText.text = preventionItem.text
    }
  }
}