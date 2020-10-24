package com.arsvechkarev.registration.presentation

import android.view.Gravity
import android.widget.EditText
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
import com.arsvechkarev.viewdsl.font
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.padding
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textColor
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.SingInButton
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Failure.FailureReason.NO_CONNECTION
import core.Failure.FailureReason.TIMEOUT
import core.Failure.FailureReason.UNKNOWN
import core.Loading
import core.viewbuilding.Colors
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
          text("Log in")
          font(Fonts.SegoeUiBold)
        }
        child<TextView>(MatchParent, WrapContent) {
          gravity(Gravity.CENTER)
          margins(start = 24.dp, end = 24.dp)
          textSize(TextSizes.H4)
          text("Enter your email and you will receive link to sign up or log in to the app")
          font(Fonts.SegoeUi)
        }
        child<EditText>(MatchParent, WrapContent) {
          tag(EditTextEmail)
          isEnabled = false
          margins(start = 20.dp, end = 20.dp, top = 40.dp, bottom = 20.dp)
          font(Fonts.SegoeUi)
          textSize(TextSizes.H3)
          padding(12.dp)
          setHint(R.string.hint_edit_text_email)
        }
        child<TextView>(WrapContent, WrapContent) {
          tag(TextEmailError)
          invisible()
          gravity(Gravity.CENTER)
          margins(start = 32.dp, end = 32.dp)
          textColor(Colors.Failure)
          textSize(TextSizes.H4)
          font(Fonts.SegoeUi)
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
        child<TextView>(MatchParent, WrapContent) {
          tag(TextTimer)
          invisible()
          textColor(Colors.TextSecondary)
          gravity(Gravity.CENTER)
          margins(top = 20.dp, start = 24.dp, end = 24.dp)
          textSize(TextSizes.H4)
          font(Fonts.SegoeUi)
        }
      }
      child<SingInButton>(MatchParent, WrapContent) {
        tag(ButtonSignIn)
        margins(start = 20.dp, end = 20.dp, bottom = 20.dp)
        layoutGravity(Gravity.BOTTOM)
        onClick {
          val email = editText(EditTextEmail).text.toString()
          viewModel.sendEmailLink(email)
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
      is InitialState -> renderInitialState()
      is Loading -> renderLoading()
      is EmailLinkSent -> renderEmailLinkSent(state)
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderInitialState() {
    view(EditTextEmail).isEnabled = true
    view(EditTextEmail).requestFocus()
  }
  
  private fun renderLoading() {
    view(TextEmailError).animateInvisible()
    viewAs<SingInButton>(ButtonSignIn).showProgress()
    view(ButtonSignIn).isClickable = false
  }
  
  private fun renderEmailLinkSent(state: EmailLinkSent) {
    editText(EditTextEmail).text(state.email)
    viewAs<SingInButton>(ButtonSignIn).hideProgress()
    view(TextLinkWasSent).animateVisible()
    view(TextTimer).animateVisible()
    view(ButtonSignIn).isEnabled = false
    view(EditTextEmail).isEnabled = false
  }
  
  private fun renderFailure(state: Failure) {
    Timber.d(state.throwable, "Registration error")
    view(ButtonSignIn).isClickable = true
    if (state.throwable is FirebaseAuthInvalidCredentialsException) {
      textView(TextEmailError).text(R.string.error_email_is_incorrect)
    } else {
      val textRes = when (state.reason) {
        NO_CONNECTION -> R.string.text_no_connection_short
        TIMEOUT -> R.string.text_timeout_short
        UNKNOWN -> R.string.text_unknown_error_short
      }
      textView(TextEmailError).text(textRes)
    }
    textView(TextEmailError).animateVisible()
    viewAs<SingInButton>(ButtonSignIn).hideProgress()
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
  
  private companion object {
    
    const val EditTextEmail = "EditTextEmail"
    const val ButtonSignIn = "ButtonSignIn"
    const val TextTimer = "TextTimer"
    const val TextEmailError = "TextEmailError"
    const val TextLinkWasSent = "TextLinkWasSent"
  }
}