package com.arsvechkarev.stats.presentation

import androidx.lifecycle.Observer
import com.arsvechkarev.stats.R
import com.arsvechkarev.stats.di.StatsModuleInjector
import com.arsvechkarev.viewdsl.animateChildrenInvisible
import com.arsvechkarev.viewdsl.animateChildrenVisible
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.behavior
import com.arsvechkarev.viewdsl.getBehavior
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.paddings
import com.arsvechkarev.viewdsl.statusBarHeight
import com.arsvechkarev.viewdsl.visible
import com.arsvechkarev.views.CustomNestedScrollView
import com.arsvechkarev.views.behaviors.ScrollableContentBehavior
import com.arsvechkarev.views.behaviors.TitleHeaderBehavior
import com.arsvechkarev.views.charts.DailyCasesChart.Type.NEW_CASES
import com.arsvechkarev.views.charts.DailyCasesChart.Type.TOTAL_CASES
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import com.arsvechkarev.views.drawables.MainStatsInfoLoadingStub
import com.arsvechkarev.views.drawables.StatsGraphLoadingStub
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.Failure.FailureReason.NO_CONNECTION
import core.Failure.FailureReason.TIMEOUT
import core.Failure.FailureReason.UNKNOWN
import core.Loading
import core.hostActivity
import core.model.GeneralInfo
import core.model.WorldCasesInfo
import kotlinx.android.synthetic.main.fragment_stats.statsContentView
import kotlinx.android.synthetic.main.fragment_stats.statsErrorLayout
import kotlinx.android.synthetic.main.fragment_stats.statsErrorMessage
import kotlinx.android.synthetic.main.fragment_stats.statsGeneralStatsView
import kotlinx.android.synthetic.main.fragment_stats.statsHeader
import kotlinx.android.synthetic.main.fragment_stats.statsIconDrawer
import kotlinx.android.synthetic.main.fragment_stats.statsImageFailure
import kotlinx.android.synthetic.main.fragment_stats.statsMainInfoLoadingStub
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesLabel
import kotlinx.android.synthetic.main.fragment_stats.statsNewCasesLoadingStub
import kotlinx.android.synthetic.main.fragment_stats.statsRetryButton
import kotlinx.android.synthetic.main.fragment_stats.statsScrollingContentView
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesChart
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesLabel
import kotlinx.android.synthetic.main.fragment_stats.statsTotalCasesLoadingStub
import timber.log.Timber

class StatsFragment : BaseFragment(R.layout.fragment_stats) {
  
  private var viewModel: StatsViewModel? = null
  
  override fun onInit() {
    viewModel = StatsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    statsHeader.paddings(top = requireContext().statusBarHeight)
    initViews()
    initClickListeners()
  }
  
  override fun onDrawerOpened() = toggleScrollingContent(false)
  
  override fun onDrawerClosed() {
    val state = viewModel?.state?.value
    if (state is Loading || state is Failure) {
      toggleScrollingContent(false)
    } else {
      toggleScrollingContent(true)
    }
  }
  
  override fun onOrientationBecameLandscape() {
    statsImageFailure.gone()
  }
  
  override fun onOrientationBecamePortrait() {
    statsImageFailure.visible()
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    (statsMainInfoLoadingStub.background as? MainStatsInfoLoadingStub)?.release()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
        updateContentView(putLoading = true)
      }
      is LoadedWorldCasesInfo -> {
        hostActivity.enableTouchesOnDrawer()
        toggleScrollingContent(enable = true)
        renderGeneralInfo(state.worldCasesInfo.generalInfo)
        renderCharts(state.worldCasesInfo)
      }
      is Failure -> {
        renderFailure(state)
      }
    }
  }
  
  private fun renderCharts(info: WorldCasesInfo) {
    statsTotalCasesChart.update(info.totalDailyCases, offset = 1)
    statsNewCasesChart.update(info.newDailyCases)
    statsTotalCasesChart.animateVisible(andThen = { statsTotalCasesLoadingStub.background = null })
    statsNewCasesChart.animateVisible(andThen = { statsNewCasesLoadingStub.background = null })
    statsTotalCasesLabel.animateVisible()
    statsNewCasesLabel.animateVisible()
  }
  
  private fun renderGeneralInfo(generalInfo: GeneralInfo) {
    statsGeneralStatsView.updateNumbers(generalInfo)
    statsGeneralStatsView.animateVisible(andThen = { statsMainInfoLoadingStub.background = null })
  }
  
  private fun renderFailure(failure: Failure) {
    val reason = failure.reason
    Timber.w(failure.throwable)
    hostActivity.enableTouchesOnDrawer()
    updateContentView(putLoading = false)
    statsErrorMessage.setText(reason.getStringRes())
    when (reason) {
      TIMEOUT, UNKNOWN -> statsImageFailure.setImageResource(R.drawable.image_unknown_error)
      NO_CONNECTION -> statsImageFailure.setImageResource(R.drawable.image_no_connection)
    }
  }
  
  private fun updateContentView(putLoading: Boolean) {
    toggleScrollingContent(enable = false)
    if (putLoading) {
      statsErrorLayout.animateInvisible()
      statsContentView.animateChildrenVisible()
    } else {
      statsErrorLayout.animateVisible()
      statsContentView.animateChildrenInvisible()
    }
  }
  
  private fun toggleScrollingContent(enable: Boolean) {
    val behavior = statsScrollingContentView.getBehavior<ScrollableContentBehavior<*>>()
    behavior.respondToTouches = enable
    statsScrollingContentView.isEnabled = enable
    statsNewCasesChart.isEnabled = enable
    statsTotalCasesChart.isEnabled = enable
  }
  
  private fun initClickListeners() {
    val onDown = { hostActivity.disableTouchesOnDrawer() }
    val onUp = { hostActivity.enableTouchesOnDrawer() }
    statsTotalCasesChart.onDown = onDown
    statsNewCasesChart.onDown = onDown
    statsTotalCasesChart.onUp = onUp
    statsNewCasesChart.onUp = onUp
    statsIconDrawer.setOnClickListener { hostActivity.openDrawer() }
    statsTotalCasesChart.onDailyCaseClicked { statsTotalCasesLabel.drawCase(it) }
    statsNewCasesChart.onDailyCaseClicked { statsNewCasesLabel.drawCase(it) }
    statsRetryButton.setOnClickListener { viewModel!!.startLoadingData() }
  }
  
  private fun initViews() {
    statsTotalCasesChart.type = TOTAL_CASES
    statsNewCasesChart.type = NEW_CASES
    statsHeader.behavior(TitleHeaderBehavior { it.id == R.id.statsScrollingContentView })
    statsScrollingContentView.behavior(
      ScrollableContentBehavior<CustomNestedScrollView>(requireContext()))
    statsMainInfoLoadingStub.setLoadingDrawable(MainStatsInfoLoadingStub(requireContext()))
    statsTotalCasesLoadingStub.setLoadingDrawable(StatsGraphLoadingStub(requireContext()))
    statsNewCasesLoadingStub.setLoadingDrawable(StatsGraphLoadingStub(requireContext()))
  }
}