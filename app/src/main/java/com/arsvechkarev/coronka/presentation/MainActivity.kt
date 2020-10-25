package com.arsvechkarev.coronka.presentation

import android.graphics.drawable.GradientDrawable.Orientation.BL_TR
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.arsvechkarev.coronka.R
import com.arsvechkarev.coronka.di.MainModuleInjector
import com.arsvechkarev.registration.presentation.RegistrationFragment
import com.arsvechkarev.registration.presentation.RegistrationFragment.Companion.EMAIL_KEY
import com.arsvechkarev.stats.presentation.StatsFragment
import com.arsvechkarev.viewdsl.Densities
import com.arsvechkarev.viewdsl.Ints.dp
import com.arsvechkarev.viewdsl.Size.Companion.MatchParent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.backgroundGradient
import com.arsvechkarev.viewdsl.drawables
import com.arsvechkarev.viewdsl.gravity
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.layoutGravity
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.orientation
import com.arsvechkarev.viewdsl.size
import com.arsvechkarev.viewdsl.tag
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.textSize
import com.arsvechkarev.viewdsl.withViewBuilder
import com.arsvechkarev.views.DrawerGroupLinearLayout
import com.arsvechkarev.views.DrawerLayout
import core.BaseActivity
import core.BaseScreenState
import core.ConnectivityObserver
import core.HostActivity
import core.extenstions.connectivityManager
import core.navigation.Navigator
import core.viewbuilding.Colors
import core.viewbuilding.Colors.GradientHeaderEnd
import core.viewbuilding.Colors.GradientHeaderStart
import core.viewbuilding.Styles.BoldTextView
import core.viewbuilding.Styles.DrawerTextView
import core.viewbuilding.TextSizes

class MainActivity : BaseActivity(), HostActivity {
  
  private fun buildLayout() = withViewBuilder {
    DrawerLayout(context).apply {
      tag(DrawerLayout)
      size(MatchParent, MatchParent)
      child<FrameLayout, LayoutParams>(MatchParent, MatchParent) {
        addLoadingLayout().apply {
          layoutGravity(Gravity.CENTER)
          invisible()
        }
        addFailureLayout().apply {
          layoutGravity(Gravity.CENTER)
          invisible()
        }
        child<FrameLayout, LayoutParams>(MatchParent, MatchParent) {
          id = R.id.fragmentContainer
        }
      }
      child<DrawerGroupLinearLayout, LayoutParams>(MatchParent, MatchParent) {
        tag(DrawerGroupLinearLayout)
        orientation(LinearLayout.VERTICAL)
        backgroundGradient(BL_TR, GradientHeaderStart, GradientHeaderEnd)
        child<TextView>(WrapContent, WrapContent, style = BoldTextView) {
          margins(start = 16.dp, top = 24.dp, bottom = 24.dp)
          drawables(start = R.drawable.logo_icon)
          compoundDrawablePadding = 16.dp
          gravity(Gravity.CENTER)
          text(R.string.title_covid_19)
          textSize(TextSizes.H0)
        }
        drawerTextVew(TextStatistics, R.drawable.ic_statistics, R.string.label_stats)
        drawerTextVew(TextNews, R.drawable.ic_newspaper, R.string.label_news)
        drawerTextVew(TextMap, R.drawable.ic_map, R.string.label_map)
        drawerTextVew(TextRankings, R.drawable.ic_rankings, R.string.label_rankings)
        drawerTextVew(TextTips, R.drawable.ic_tips, R.string.label_tips)
      }
    }
  }
  
  private fun DrawerGroupLinearLayout.drawerTextVew(tag: String, drawableRes: Int, textRes: Int) =
      addView(TextView(context).apply {
        size(MatchParent, WrapContent)
        apply(DrawerTextView)
        tag(tag)
        drawables(start = drawableRes)
        text(textRes)
      })
  
  private lateinit var viewModel: MainViewModel
  private lateinit var navigator: Navigator
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Densities.init(resources)
    Colors.init(this)
    supportActionBar?.hide()
    setContentView(buildLayout())
    navigator = MainModuleInjector.provideNavigator(this, DrawerLayout)
    viewModel = MainModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      if (savedInstanceState == null) {
        model.figureOutScreenToGo(intent)
      }
    }
    val connectivityObserver = ConnectivityObserver(connectivityManager, onNetworkAvailable = {
      navigator.currentFragment?.onNetworkAvailable()
    })
    lifecycle.addObserver(navigator)
    lifecycle.addObserver(connectivityObserver)
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = false
    initListeners()
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
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is GoToRegistrationScreen -> renderGoToRegistrationScreen()
      is GoToMainScreen -> goToMainFragment()
      is ShowEmailLinkLoading -> renderEmailLinkLoading()
      is SuccessfullySignedId -> renderSignedIn()
      is NoEmailSaved -> renderNoEmail()
      is VerificationLinkExpired -> renderLinkExpired(state)
      is EmailVerificationFailure -> renderFailure(state)
    }
  }
  
  private fun renderGoToRegistrationScreen() {
    navigator.switchTo(RegistrationFragment::class)
  }
  
  private fun goToMainFragment() {
    viewAs<DrawerLayout>(DrawerLayout).respondToTouches = true
    view(TextStatistics).isSelected = true
    navigator.switchTo(StatsFragment::class)
  }
  
  private fun renderEmailLinkLoading() {
    view(LayoutLoading).animateVisible()
  }
  
  private fun renderSignedIn() {
    view(LayoutLoading).animateInvisible()
    goToMainFragment()
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
    
    const val DrawerLayout = "DrawerLayout"
    const val DrawerGroupLinearLayout = "DrawerGroupLinearLayout"
    const val TextStatistics = "TextStatistics"
    const val TextNews = "TextNews"
    const val TextMap = "TextMap"
    const val TextRankings = "TextRankings"
    const val TextTips = "TextTips"
  }
}