package com.arsvechkarev.registration.presentation

import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.arsvechkarev.registration.R
import com.arsvechkarev.registration.di.RegistrationModuleInjector
import com.arsvechkarev.views.RegistrationButton
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Loading
import core.viewbuilding.Colors
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes
import viewdsl.Ints.dp
import viewdsl.Size.Companion.MatchParent
import viewdsl.Size.Companion.WrapContent
import viewdsl.backgroundColor
import viewdsl.backgroundRoundRect
import viewdsl.font
import viewdsl.layoutGravity
import viewdsl.margin
import viewdsl.margins
import viewdsl.onClick
import viewdsl.orientation
import viewdsl.paddingHorizontal
import viewdsl.paddingVertical
import viewdsl.tag
import viewdsl.text
import viewdsl.textSize
import viewdsl.withViewBuilder

class RegistrationFragment : BaseFragment() {
  
  override fun buildLayout() = withViewBuilder {
    CoordinatorLayout(MatchParent, MatchParent) {
      backgroundColor(Colors.Background)
      child<LinearLayout>(MatchParent, WrapContent) {
        layoutGravity(Gravity.CENTER)
        orientation(LinearLayout.VERTICAL)
        child<TextView>(WrapContent, WrapContent) {
          layoutGravity(Gravity.CENTER)
          margin(24.dp)
          textSize(TextSizes.MainTitle)
          text(R.string.app_name)
          font(Fonts.SegoeUiBold)
        }
        child<EditText>(MatchParent, WrapContent) {
          tag(EditTextEmail)
          backgroundRoundRect(60.dp, Colors.Overlay)
          margin(20.dp)
          font(Fonts.SegoeUi)
          textSize(TextSizes.H3)
          paddingVertical(12.dp)
          paddingHorizontal(24.dp)
          setHint(R.string.hint_edit_text_email)
        }
        child<RegistrationButton>(MatchParent, WrapContent) {
          margins(start = 20.dp, end = 20.dp)
          text(R.string.text_sign_in)
          onClick { proceedWithRegistration() }
        }
      }
    }
  }
  
  private var viewModel: RegistrationViewModel? = null
  
  override fun onInit() {
    viewModel = RegistrationModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
    }
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_LONG).show()
      }
      is EmailLinkSent -> {
        Toast.makeText(requireContext(), "EmailLinkSent", Toast.LENGTH_LONG).show()
      }
      is Failure -> {
        Toast.makeText(requireContext(), "Failure", Toast.LENGTH_LONG).show()
      }
    }
  }
  
  private fun proceedWithRegistration() {
    val email = viewAs<EditText>(EditTextEmail).text.toString()
    viewModel!!.sendEmailLink(email)
  }
  
  companion object {
    
    private const val EditTextEmail = "EditTextEmail"
  }
}