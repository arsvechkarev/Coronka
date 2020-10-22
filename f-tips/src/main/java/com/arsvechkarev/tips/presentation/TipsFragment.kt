package com.arsvechkarev.tips.presentation

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.tips.R
import com.arsvechkarev.tips.presentation.TipsDimens.FAQItemCornersRadius
import com.arsvechkarev.tips.presentation.TipsDimens.FAQItemMarin
import com.arsvechkarev.tips.presentation.TipsDimens.FAQItemPadding
import com.arsvechkarev.tips.presentation.TipsDimens.FAQItemPaddingStart
import com.arsvechkarev.tips.presentation.TipsDimens.HeaderMarginBottom
import com.arsvechkarev.tips.presentation.TipsDimens.HeaderMarginStart
import com.arsvechkarev.tips.presentation.TipsDimens.HeaderMarginTop
import com.arsvechkarev.tips.presentation.TipsDimens.PreventionsLayoutHorizontalMargin
import com.arsvechkarev.tips.presentation.TipsDimens.PreventionsLayoutImagePadding
import com.arsvechkarev.tips.presentation.TipsDimens.PreventionsLayoutTextImageSize
import com.arsvechkarev.tips.presentation.TipsDimens.PreventionsLayoutTextPadding
import com.arsvechkarev.tips.presentation.TipsDimens.PreventionsLayoutVerticalMargin
import com.arsvechkarev.tips.presentation.TipsDimens.SymptomsItemPadding
import com.arsvechkarev.views.PreventionView
import com.arsvechkarev.views.SymptomsView
import com.arsvechkarev.views.behaviors.BottomSheetBehavior
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import core.BaseFragment
import core.hostActivity
import core.recycler.StaticDelegateBuilder
import core.recycler.adapter
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.GradientHeaderHeight
import core.viewbuilding.Dimens.ImageDrawerMargin
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.Styles.HeaderTextView
import core.viewbuilding.TextSizes
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheet
import kotlinx.android.synthetic.main.fragment_tips.tipsBottomSheetCross
import kotlinx.android.synthetic.main.fragment_tips.tipsRecyclerView
import kotlinx.android.synthetic.main.fragment_tips.tipsTextAnswer
import kotlinx.android.synthetic.main.fragment_tips.tipsTextTitle
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.Size.IntSize
import viewdsl.background
import viewdsl.drawables
import viewdsl.gravity
import viewdsl.image
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.margins
import viewdsl.onClick
import viewdsl.paddings
import viewdsl.rippleBackground
import viewdsl.size
import viewdsl.text
import viewdsl.textSize

class TipsFragment : BaseFragment(R.layout.fragment_tips) {
  
  override fun onInit() {
    val behavior = (tipsBottomSheet.layoutParams as CoordinatorLayout.LayoutParams).behavior!!
    val bottomSheetBehavior = behavior as BottomSheetBehavior<View>
    behavior.onHide = { hostActivity.enableTouchesOnDrawer() }
    behavior.onShow = { hostActivity.disableTouchesOnDrawer() }
    tipsBottomSheetCross.setOnClickListener { bottomSheetBehavior.hide() }
    val adapter = adapter {
      delegate(MainItem::class, mainHeaderLayout())
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_faq)))
      delegate(FAQItem::class, faqItemLayout(behavior))
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_symptoms)))
      delegate(SymptomsItem::class, symptomsLayout())
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_prevention_tips)))
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
  
  private fun mainHeaderLayout(): StaticDelegateBuilder<MainItem>.() -> Unit = {
    data(MainItem)
    buildView {
      FrameLayout(MatchParent, IntSize(GradientHeaderHeight)) {
        background(GradientHeaderDrawable())
        child<TextView>(WrapContent, WrapContent, style = HeaderTextView) {
          text(R.string.title_tips)
          layoutGravity(Gravity.CENTER)
        }
        child<ImageView>(WrapContent, WrapContent) {
          margins(start = ImageDrawerMargin, top = ImageDrawerMargin)
          image(R.drawable.ic_drawer)
          onClick { hostActivity.openDrawer() }
        }
      }
    }
  }
  
  private fun headerLayout(title: String): StaticDelegateBuilder<HeaderItem>.() -> Unit = {
    data(HeaderItem(title))
    buildView {
      TextView(WrapContent, WrapContent, style = BoldTextView) {
        margins(
          start = HeaderMarginStart,
          top = HeaderMarginTop,
          bottom = HeaderMarginBottom
        )
        textSize(TextSizes.H2)
      }
    }
    onBind { view, item ->
      (view as TextView).text(item.title)
    }
  }
  
  private fun symptomsLayout(): StaticDelegateBuilder<SymptomsItem>.() -> Unit = {
    data(SymptomsItem)
    buildView {
      SymptomsView(context).apply {
        paddings(
          start = SymptomsItemPadding,
          end = SymptomsItemPadding,
          top = SymptomsItemPadding
        )
      }
    }
  }
  
  private fun faqItemLayout(behavior: BottomSheetBehavior<*>): StaticDelegateBuilder<FAQItem>.() -> Unit = {
    data(listOf(
      FAQItem(R.string.q1, R.string.a1),
      FAQItem(R.string.q2, R.string.a2),
      FAQItem(R.string.q3, R.string.a3),
      FAQItem(R.string.q4, R.string.a4),
      FAQItem(R.string.q5, R.string.a5)
    ))
    buildView {
      TextView(MatchParent, WrapContent, style = BoldTextView) {
        margin(FAQItemMarin)
        rippleBackground(
          rippleColor = Colors.Ripple,
          backgroundColor = Colors.Overlay,
          cornerRadius = FAQItemCornersRadius
        )
        paddings(
          start = FAQItemPaddingStart,
          top = FAQItemPadding,
          end = FAQItemPadding,
          bottom = FAQItemPadding
        )
        drawables(end = R.drawable.ic_chevron_right)
        gravity(Gravity.CENTER_VERTICAL)
        textSize(TextSizes.H4)
      }
    }
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
      (view as TextView).text(item.questionLayoutRes)
    }
  }
  
  private fun preventionsLayout(): StaticDelegateBuilder<PreventionItem>.() -> Unit = {
    data(listOf(
      PreventionItem(R.drawable.image_no_touch, R.string.text_prevention_no_touch),
      PreventionItem(R.drawable.image_protection, R.string.text_prevention_protection),
      PreventionItem(R.drawable.image_wash_hands, R.string.text_prevention_wash_hands),
      PreventionItem(R.drawable.image_social_distancing, R.string.text_prevention_social_distancing)
    ))
    buildView {
      PreventionView(
        context,
        PreventionsLayoutImagePadding,
        PreventionsLayoutTextPadding,
        PreventionsLayoutTextImageSize
      ).apply {
        size(MatchParent, WrapContent)
        margins(
          start = PreventionsLayoutHorizontalMargin,
          top = PreventionsLayoutVerticalMargin,
          end = PreventionsLayoutHorizontalMargin,
          bottom = PreventionsLayoutVerticalMargin
        )
      }
    }
    onBind { view, item ->
      (view as PreventionView).setData(item.imageRes, item.textRes)
    }
  }
}