package com.arsvechkarev.registration.presentation

import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.arsvechkarev.registration.R
import com.arsvechkarev.registration.di.RegistrationModuleInjector
import com.arsvechkarev.registration.presentation.EmailState.Correct
import com.arsvechkarev.registration.presentation.EmailState.Incorrect
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.backgroundColor
import com.arsvechkarev.viewdsl.backgroundRoundRect
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.paddingHorizontal
import com.arsvechkarev.viewdsl.paddingVertical
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textColor
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.MaterialProgressBar
import com.arsvechkarev.views.SingInButton
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Loading
import core.viewbuilding.Colors
import core.viewbuilding.Dimens.ProgressBarSize
import core.viewbuilding.Fonts
import core.viewbuilding.TextSizes
import timber.log.Timber

class RegistrationFragment : BaseFragment() {
  
  override fun buildLayout() = withViewBuilder {
    FrameLayout(MatchParent, MatchParent) {
      clipChildren = false
      backgroundColor(Colors.Background)
      child<LinearLayout>(MatchParent, WrapContent) {
        clipChildren = false
        layoutGravity(Gravity.CENTER)
        orientation(LinearLayout.VERTICAL)
        child<TextView>(WrapContent, WrapContent) {
          layoutGravity(Gravity.CENTER)
          margins(bottom = 24.dp)
          textSize(TextSizes.MainTitle)
          text(R.string.app_name)
          font(Fonts.SegoeUiBold)
        }
        child<TextView>(WrapContent, WrapContent) {
          tag(TextEmailError)
          invisible()
          gravity(Gravity.CENTER)
          margins(start = 32.dp, end = 32.dp)
          textColor(Colors.Failure)
          textSize(TextSizes.H3)
          font(Fonts.SegoeUi)
        }
        child<EditText>(MatchParent, WrapContent) {
          tag(EditTextEmail)
          backgroundRoundRect(60.dp, Colors.Overlay)
          margins(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 20.dp)
          font(Fonts.SegoeUi)
          textSize(TextSizes.H3)
          paddingVertical(12.dp)
          paddingHorizontal(24.dp)
          setHint(R.string.hint_edit_text_email)
        }
        child<SingInButton>(MatchParent, WrapContent) {
          tag(ButtonSignIn)
          margins(start = 20.dp, end = 20.dp)
          text(R.string.text_sign_in)
          onClick {
            val email = editText(EditTextEmail).text.toString()
            viewModel.sendEmailLink(email)
          }
        }
        child<FrameLayout>(MatchParent, WrapContent) {
          clipChildren = false
          margins(top = 20.dp)
          child<MaterialProgressBar>(ProgressBarSize, ProgressBarSize) {
            tag(ProgressBar)
            invisible()
            layoutGravity(Gravity.CENTER)
          }
          child<TextView>(MatchParent, WrapContent) {
            tag(TextLinkWasSent)
            invisible()
            gravity(Gravity.CENTER)
            margins(top = 20.dp, start = 24.dp, end = 24.dp)
            textSize(TextSizes.H3)
            text(R.string.text_email_sent)
            font(Fonts.SegoeUi)
          }
        }
        child<TextView>(MatchParent, WrapContent) {
          tag(TextTimer)
          invisible()
          gravity(Gravity.CENTER)
          margins(top = 20.dp, start = 24.dp, end = 24.dp)
          textSize(TextSizes.H4)
          font(Fonts.SegoeUi)
        }
      }
    }
  }
  
  private lateinit var viewModel: RegistrationViewModel
  
  override fun onInit() {
    viewModel = RegistrationModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.emailState.observe(this, Observer(::handleEmailState))
      model.timerState.observe(this, Observer(::handleTimerState))
    }
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> renderLoading()
      is EmailLinkSent -> renderEmailLinkSent()
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderEmailLinkSent() {
    view(ProgressBar).animateInvisible(andThen = {
      view(TextLinkWasSent).animateVisible()
      view(TextTimer).animateVisible()
      view(ButtonSignIn).isEnabled = false
      view(EditTextEmail).isEnabled = false
    })
  }
  
  private fun renderLoading() {
    view(TextEmailError).animateInvisible()
    view(ProgressBar).animateVisible()
    view(ButtonSignIn).isClickable = false
  }
  
  private fun renderFailure(state: Failure) {
    Timber.d(state.throwable, "Registration error")
    view(ButtonSignIn).isClickable = true
    if (state.throwable is FirebaseAuthInvalidCredentialsException) {
      textView(TextEmailError).text(R.string.error_email_is_incorrect)
    } else {
      textView(TextEmailError).text(R.string.error_email_is_incorrect)
    }
    textView(TextEmailError).animateVisible()
    view(ProgressBar).animateInvisible()
  }
  
  private fun handleEmailState(state: EmailState) {
    when (state) {
      is Correct -> {
        view(TextEmailError).animateInvisible()
      }
      is Incorrect -> {
        textView(TextEmailError).text(state.errorMsgResId)
        textView(TextEmailError).animateVisible()
      }
    }
  }
  
  private fun handleTimerState(state: TimerState) {
    when (state) {
      is TimerState.TimeIsTicking -> {
        textView(TextTimer).text("Resend link in ${state.time}")
      }
      is TimerState.TimerHasFinished -> {
        view(TextTimer).animateInvisible()
        view(TextLinkWasSent).animateInvisible()
        view(ButtonSignIn).isEnabled = true
        view(EditTextEmail).isEnabled = true
        view(ButtonSignIn).isClickable = true
      }
    }
  }
  
  companion object {
    
    private const val EditTextEmail = "EditTextEmail"
    private const val ButtonSignIn = "ButtonSignIn"
    private const val ProgressBar = "ProgressBar"
    private const val TextTimer = "TextTimer"
    private const val TextEmailError = "TextEmailError"
    private const val TextLinkWasSent = "TextLinkWasSent"
  }
}