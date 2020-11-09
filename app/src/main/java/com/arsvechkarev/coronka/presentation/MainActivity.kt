package com.arsvechkarev.coronka.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.di.MainModuleInjector
import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.registration.presentation.RegistrationFragment.Companion.EMAIL_KEY
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.viewdsl.Densities
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.views.CheckmarkView
import com.arsvechkarev.views.DrawerGroupLinearLayout
import com.arsvechkarev.views.DrawerLayout
import com.arsvechkarev.views.DrawerLayout.DrawerState.OPENED
import core.BaseActivity
import core.BaseScreenState
import core.ConnectivityObserver
import core.HostActivity
import core.Loading
import core.extenstions.connectivityManager
import core.navigation.Navigator
import core.viewbuilding.Colors

class MainActivity : BaseActivity(), HostActivity {
  
  private lateinit var viewModel: MainViewModel
  private lateinit var navigator: Navigator
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    supportActionBar?.hide()
    setContentView(buildMainActivityLayout())
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    navigator = MainModuleInjector.provideNavigator(this, DrawerLayout)
    viewModel = MainModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.figureOutScreenToGo(intent)
    }
    val connectivityObserver = ConnectivityObserver(connectivityManager, onNetworkAvailable = {
      navigator.currentFragment?.onNetworkAvailable()
    })
    lifecycle.addObserver(navigator)
    lifecycle.addObserver(connectivityObserver)
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = false
    initListeners()
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  }
  
  override fun openDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).open()
  }
  
  override fun enableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = true
  }
  
  override fun disableTouchesOnDrawer() {
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = false
  }
  
  override fun onBackPressed() {
    val drawerLayout = viewAs<DrawerLayout>(DrawerLayout)
    if (drawerLayout.state == OPENED) {
      drawerLayout.close()
      return
    }
    if (navigator.allowBackPress()) {
      super.onBackPressed()
    }
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is GoToRegistrationScreen -> renderGoToRegistrationScreen()
      is GoToMainScreen -> goToMainFragment()
      is Loading -> renderLoading()
      is SuccessfullySignedId -> renderSignedIn()
      is NoEmailSaved -> renderNoEmail()
      is VerificationLinkExpired -> renderLinkExpired(state)
      is EmailVerificationFailure -> renderFailure(state)
    }
  }
  
  private fun renderGoToRegistrationScreen() {
    navigator.navigateTo(RegistrationFragment::class)
  }
  
  private fun goToMainFragment() {
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = true
    view(TextStatistics).isSelected = true
    navigator.switchTo(StatsFragment::class)
  }
  
  private fun renderLoading() {
    textView(TextVerifyingLink).text(R.string.text_verifying_link)
    view(LayoutLoading).animateVisible()
    view(LayoutError).animateInvisible()
  }
  
  private fun renderSignedIn() {
    view(ProgressBar).animateInvisible(andThen = {
      textView(TextVerifyingLink).text(R.string.text_successfully_verified)
      viewAs<CheckmarkView>(CheckmarkView).animateCheckmark(andThen = {
        view(LayoutLoading).animateInvisible(andThen = {
          goToMainFragment()
        })
      })
    })
  }
  
  private fun renderNoEmail() {
    showFailureLayout(
      R.string.error_while_checking_link,
      R.string.text_retry,
      onClickAction = {
        navigator.navigateTo(RegistrationFragment::class)
      })
  }
  
  private fun renderLinkExpired(state: VerificationLinkExpired) {
    showFailureLayout(
      R.string.error_email_link_expired,
      R.string.text_resend_link,
      onClickAction = {
        navigator.navigateTo(RegistrationFragment::class, Bundle().apply {
          putString(EMAIL_KEY, state.email)
        })
      })
  }
  
  private fun renderFailure(state: EmailVerificationFailure) {
    showFailureLayout(
      state.reason.getStringRes(),
      R.string.text_retry,
      onClickAction = {
        viewModel.figureOutScreenToGo(intent)
      })
  }
  
  private fun showFailureLayout(
    textErrorRes: Int,
    textButtonRetryRes: Int,
    onClickAction: () -> Unit
  ) {
    view(LayoutLoading).animateInvisible()
    view(LayoutError).animateVisible()
    textView(TextError).text(textErrorRes)
    textView(ButtonRetry).text(textButtonRetryRes)
    textView(ButtonRetry).onClick(onClickAction)
  }
  
  private fun initListeners() {
    val onDrawerItemClick: (v: View) -> Unit = { view ->
      viewAs<DrawerGroupLinearLayout>(DrawerGroupLinearLayout).onTextViewClicked(view)
      navigator.handleOnDrawerItemClicked(view.tag as String)
    }
    view(TextStatistics).setOnClickListener(onDrawerItemClick)
    view(TextNews).setOnClickListener(onDrawerItemClick)
    view(TextMap).setOnClickListener(onDrawerItemClick)
    view(TextRankings).setOnClickListener(onDrawerItemClick)
    view(TextTips).setOnClickListener(onDrawerItemClick)
  }
  
  companion object {
  
    const val LayoutLoading = "LayoutLoading"
    const val LayoutError = "LayoutError"
    const val TextError = "TextError"
    const val ButtonRetry = "ButtonRetry"
    const val ProgressBar = "ProgressBar"
    const val CheckmarkView = "CheckmarkView"
    const val TextVerifyingLink = "TextVerifyingLink"
  
    const val DrawerLayout = "DrawerLayout"
    const val DrawerGroupLinearLayout = "DrawerGroupLinearLayout"
    const val TextStatistics = "TextStatistics"
    const val TextNews = "TextNews"
    const val TextMap = "TextMap"
    const val TextRankings = "TextRankings"
    const val TextTips = "TextTips"
  }
}