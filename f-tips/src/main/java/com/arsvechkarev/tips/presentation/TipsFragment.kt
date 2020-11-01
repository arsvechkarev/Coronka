package com.arsvechkarev.tips.presentation

import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.Size.IntSize
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.behavior
import com.arsvechkarev.viewdsl.childTextView
import com.arsvechkarev.viewdsl.childView
import com.arsvechkarev.viewdsl.constraints
import com.arsvechkarev.viewdsl.drawables
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.id
import com.arsvechkarev.viewdsl.image
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margin
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.rippleBackground
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.CustomRecyclerView
import com.arsvechkarev.views.PreventionView
import com.arsvechkarev.views.SymptomsView
import com.arsvechkarev.views.behaviors.BottomSheetBehavior
import com.arsvechkarev.views.behaviors.BottomSheetBehavior.Companion.asBottomSheet
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import core.BaseFragment
import core.hostActivity
import core.recycler.StaticDelegateBuilder
import core.recycler.adapter
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.GradientHeaderHeight
import core.viewbuilding.Dimens.ImageDrawerMargin
import core.viewbuilding.Styles.BaseTextView
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.Styles.HeaderTextView
import core.viewbuilding.TextSizes

class TipsFragment : BaseFragment() {
  
  override fun buildLayout() = withViewBuilder {
    CoordinatorLayout(MatchParent, MatchParent) {
      child<CustomRecyclerView>(MatchParent, MatchParent) {
        tag(RecyclerView)
      }
      child<ConstraintLayout>(MatchParent, WrapContent) {
        tag(BottomSheet)
        id(R.id.tipsBottomSheet)
        background(R.drawable.bg_bottom_sheet)
        behavior(BottomSheetBehavior(context).apply {
          onHide = { hostActivity.enableTouchesOnDrawer() }
          onShow = { hostActivity.disableTouchesOnDrawer() }
        })
        child<ImageView>(WrapContent, WrapContent) {
          id(R.id.tipsBottomSheetCross)
          image(R.drawable.ic_cross)
          background(R.drawable.bg_ripple)
          margin(24.dp)
          constraints {
            topToTop(parent)
            endToEnd(parent)
          }
          onClick { view(BottomSheet).asBottomSheet.hide() }
        }
        child<TextView>(IntSize(0), WrapContent) {
          id(R.id.tipsTextTitle)
          tag(TextTitle)
          margins(top = 16.dp, start = 60.dp, end = 60.dp)
          gravity(Gravity.CENTER)
          textSize(TextSizes.H1)
          constraints {
            startToStart(parent)
            topToTop(R.id.tipsBottomSheetCross)
            endToEnd(parent)
            bottomToBottom(R.id.tipsBottomSheetCross)
          }
        }
        child<TextView>(MatchParent, WrapContent, style = BaseTextView) {
          id(R.id.tipsTextAnswer)
          tag(TextAnswer)
          margin(20.dp)
          constraints {
            startToStart(parent)
            topToBottom(R.id.tipsTextTitle)
            endToEnd(parent)
            bottomToBottom(parent)
          }
        }
      }
    }
  }
  
  override fun onInit() {
    val adapter = adapter {
      delegate(MainItem::class, mainHeaderLayout())
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_faq)))
      delegate(FAQItem::class, faqItemLayout())
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_symptoms)))
      delegate(SymptomsItem::class, symptomsLayout())
      delegate(HeaderItem::class, headerLayout(getString(R.string.text_prevention_tips)))
      delegate(PreventionItem::class, preventionsLayout())
    }
    viewAs<RecyclerView>(RecyclerView).apply {
      layoutManager = LinearLayoutManager(requireContext())
      setHasFixedSize(true)
      this.adapter = adapter
    }
  }
  
  override fun onDrawerOpened() {
    view(RecyclerView).isEnabled = false
  }
  
  override fun onDrawerClosed() {
    view(RecyclerView).isEnabled = true
  }
  
  private fun mainHeaderLayout(): StaticDelegateBuilder<MainItem>.() -> Unit = {
    data(MainItem)
    buildView {
      FrameLayout(MatchParent, IntSize(GradientHeaderHeight + StatusBarHeight)) {
        background(GradientHeaderDrawable())
        child<TextView>(WrapContent, WrapContent, style = HeaderTextView) {
          text(R.string.title_tips)
          layoutGravity(Gravity.CENTER)
        }
        child<ImageView>(WrapContent, WrapContent) {
          margins(start = ImageDrawerMargin, top = ImageDrawerMargin + StatusBarHeight)
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
  
  private fun faqItemLayout(): StaticDelegateBuilder<FAQItem>.() -> Unit = {
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
        if (itemView.childView(RecyclerView).isEnabled) {
          itemView.childTextView(TextTitle).text = getString(item.questionLayoutRes)
          itemView.childTextView(TextAnswer).text = getString(item.answerLayoutRes)
          itemView.childView(BottomSheet).asBottomSheet.show()
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
  
  private companion object {
    
    const val RootLayout = "RootLayout"
    const val RecyclerView = "RecyclerView"
    const val BottomSheet = "BottomSheet"
    const val TextTitle = "TextTitle"
    const val TextAnswer = "TextAnswer"
  }
}