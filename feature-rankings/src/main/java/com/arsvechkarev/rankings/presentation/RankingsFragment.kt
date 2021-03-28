package com.arsvechkarev.rankings.presentation

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.lifecycle.Observer
import base.BaseFragment
import base.behaviors.BottomSheetBehavior
import base.behaviors.BottomSheetBehavior.Companion.asBottomSheet
import base.behaviors.BottomSheetBehavior.State.HIDDEN
import base.behaviors.BottomSheetBehavior.State.SHOWN
import base.behaviors.HeaderBehavior.Companion.asHeader
import base.drawables.BaseLoadingStub.Companion.asLoadingStub
import base.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import base.drawables.GradientHeaderDrawable
import base.drawables.RankingsListLoadingStub
import base.drawables.SelectedChipsLoadingStub
import base.extensions.doesNotContain
import base.extensions.getMessageRes
import base.extensions.ifTrue
import base.extensions.subscribeToChannel
import base.extensions.toFormattedDecimalNumber
import base.extensions.toFormattedNumber
import base.hostActivity
import base.views.Chip
import com.arsvechkarev.rankings.R
import com.arsvechkarev.rankings.di.RankingsComponent
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateInvisibleIfNeeded
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.animateVisibleIfNeeded
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.dimen
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.heightWithMargins
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.setClickable
import com.arsvechkarev.viewdsl.setupWithAdapter
import com.arsvechkarev.viewdsl.statusBarHeight
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.unspecified
import com.arsvechkarev.viewdsl.view
import com.arsvechkarev.viewdsl.visible
import core.BaseScreenState
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.FailureReason.UNKNOWN
import core.Loading
import core.di.CoreComponent.drawerStateReceivingChannel
import core.model.OptionType
import core.model.WorldRegion
import core.model.ui.CountryFullInfo
import kotlinx.android.synthetic.main.fragment_rankings.chipAfrica
import kotlinx.android.synthetic.main.fragment_rankings.chipAsia
import kotlinx.android.synthetic.main.fragment_rankings.chipConfirmed
import kotlinx.android.synthetic.main.fragment_rankings.chipDeathRate
import kotlinx.android.synthetic.main.fragment_rankings.chipDeaths
import kotlinx.android.synthetic.main.fragment_rankings.chipEurope
import kotlinx.android.synthetic.main.fragment_rankings.chipNorthAmerica
import kotlinx.android.synthetic.main.fragment_rankings.chipOceania
import kotlinx.android.synthetic.main.fragment_rankings.chipPercentInCountry
import kotlinx.android.synthetic.main.fragment_rankings.chipRecovered
import kotlinx.android.synthetic.main.fragment_rankings.chipSouthAmerica
import kotlinx.android.synthetic.main.fragment_rankings.chipWorldwide
import kotlinx.android.synthetic.main.fragment_rankings.rankingsChipOptionType
import kotlinx.android.synthetic.main.fragment_rankings.rankingsChipWorldRegion
import kotlinx.android.synthetic.main.fragment_rankings.rankingsCountryFullInfoDialogIconBack
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialog
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogGeneralView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTextDeathRate
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTextNewConfirmed
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTextNewDeaths
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTextPercentInCountry
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTitle
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDivider
import kotlinx.android.synthetic.main.fragment_rankings.rankingsErrorMessage
import kotlinx.android.synthetic.main.fragment_rankings.rankingsFabFilter
import kotlinx.android.synthetic.main.fragment_rankings.rankingsFilterDialog
import kotlinx.android.synthetic.main.fragment_rankings.rankingsFilterDialogCross
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderGradientView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderLayout
import kotlinx.android.synthetic.main.fragment_rankings.rankingsIconDrawer
import kotlinx.android.synthetic.main.fragment_rankings.rankingsImageFailure
import kotlinx.android.synthetic.main.fragment_rankings.rankingsLayoutFailure
import kotlinx.android.synthetic.main.fragment_rankings.rankingsListLayoutLoading
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRecyclerView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRetryButton
import kotlinx.android.synthetic.main.fragment_rankings.rankingsSelectedChipsLoadingStub
import timber.log.Timber

class RankingsFragment : BaseFragment(R.layout.fragment_rankings) {
  
  private val viewModel by lazy {
    RankingsComponent.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
    }
  }
  
  private val adapter = RankingsComponent.provideAdapter { country ->
    viewModel.onCountryClicked(country)
  }
  
  private val chipHelper by lazy {
    val groupWorldRegions = ChipGroup(chipWorldwide, chipEurope, chipAsia,
      chipAfrica, chipNorthAmerica, chipOceania, chipSouthAmerica)
    val groupOptionTypes = ChipGroup(chipConfirmed, chipRecovered, chipDeaths,
      chipPercentInCountry, chipDeathRate)
    ChipHelper(
      groupOptionTypes, groupWorldRegions,
      onOptionTypeSelected = viewModel::onNewOptionTypeSelected,
      onWorldRegionSelected = viewModel::onWorldRegionSelected
    )
  }
  
  override fun onInit() {
    viewModel.startLoadingData()
    rankingsRecyclerView.setupWithAdapter(adapter)
    setupAccordingToStatusBarHeight()
    setupClickListeners()
    setupBehavior()
    setupChips()
    setupDrawables()
    hostActivity.enableTouchesOnDrawer()
    subscribeToChannel(drawerStateReceivingChannel) { drawerState ->
      toggleClickableItems(enable = drawerState.isClosed)
    }
  }
  
  override fun onHiddenChanged(hidden: Boolean) {
    if (hidden) hostActivity.enableTouchesOnDrawer()
  }
  
  override fun onDestroy() {
    viewModel.onDestroy()
    super.onDestroy()
  }
  
  override fun onOrientationBecameLandscape() {
    rankingsImageFailure.gone()
  }
  
  override fun onOrientationBecamePortrait() {
    rankingsImageFailure.visible()
  }
  
  override fun allowBackPress(): Boolean {
    return viewModel.allowBackPress()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> renderLoading()
      is Success -> renderSuccess(state)
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderLoading() {
    showOnlyViewsVisible(rankingsListLayoutLoading, rankingsSelectedChipsLoadingStub)
    rankingsFabFilter.isClickable = false
    rankingsListLayoutLoading.asLoadingStub.start()
    rankingsSelectedChipsLoadingStub.asLoadingStub.start()
  }
  
  private fun renderSuccess(state: Success) {
    toggleClickableItems(enable = !state.showFilterDialog && state.countryFullInfo == null)
    showOnlyViewsVisible(rankingsRecyclerView, rankingsChipWorldRegion,
      rankingsChipOptionType, rankingsDivider)
    handleShowCountries(state)
    handleShowChips(state.worldRegion, state.optionType)
    handleShowFilterDialog(state)
    handleShowCountryInfo(state.countryFullInfo)
  }
  
  private fun handleShowCountries(state: Success) {
    if (state.isStateOld) {
      // State is old, that happens on configuration change, and in this case we want
      // recycler to be filled with data and not be empty
      adapter.submitList(state.countries)
    }
    if (state.isListChanged) {
      rankingsHeaderLayout.asHeader.animateScrollToTop(andThen = {
        adapter.changeListWithCrossFadeAnimation(state.countries)
      })
    }
  }
  
  private fun handleShowChips(worldRegion: WorldRegion, optionType: OptionType) {
    chipHelper.setSelectedOptionType(optionType)
    chipHelper.setSelectedWorldRegion(worldRegion)
    rankingsChipOptionType.text = getString(getTextIdForOptionType(optionType))
    rankingsChipWorldRegion.text = getString(getTextIdForWorldRegion(worldRegion))
    rankingsChipOptionType.colorFill = getColorForOptionType(optionType)
  }
  
  private fun handleShowFilterDialog(state: Success) {
    if (state.showFilterDialog) {
      rankingsFilterDialog.asBottomSheet.setDialogState(SHOWN, animate = !state.isStateOld)
      hostActivity.disableTouchesOnDrawer()
    } else {
      rankingsFilterDialog.asBottomSheet.setDialogState(HIDDEN, animate = !state.isStateOld)
      hostActivity.enableTouchesOnDrawer()
    }
  }
  
  private fun BottomSheetBehavior.setDialogState(state: BottomSheetBehavior.State, animate: Boolean) {
    requireView().post {
      if (animate) {
        when (state) {
          SHOWN -> show()
          HIDDEN -> hide()
        }
      } else {
        setStateImmediately(state)
      }
    }
  }
  
  private fun handleShowCountryInfo(countryFullInfo: CountryFullInfo?) {
    if (countryFullInfo != null) {
      val country = countryFullInfo.country
      rankingsDialogTitle.text(country.name)
      rankingsDialogGeneralView.updateData(country.confirmed, country.recovered, country.deaths)
      rankingsDialogTextNewConfirmed.text(country.newConfirmed.toFormattedNumber())
      rankingsDialogTextNewDeaths.text(country.newDeaths.toFormattedNumber())
      rankingsDialogTextDeathRate.text(countryFullInfo.deathRate.toFormattedDecimalNumber())
      rankingsDialogTextPercentInCountry.text(
        countryFullInfo.percentInCountry.toFormattedDecimalNumber())
      rankingsDialog.animateVisible()
    } else {
      rankingsDialog.animateInvisible()
    }
  }
  
  private fun renderFailure(state: Failure) {
    Timber.d(state.throwable)
    rankingsFabFilter.isClickable = false
    showOnlyViewsVisible(rankingsLayoutFailure)
    rankingsErrorMessage.text(state.reason.getMessageRes())
    when (state.reason) {
      TIMEOUT, UNKNOWN -> rankingsImageFailure.setImageResource(R.drawable.image_unknown_error)
      NO_CONNECTION -> rankingsImageFailure.setImageResource(R.drawable.image_no_connection)
    }
  }
  
  private fun showOnlyViewsVisible(vararg views: View) {
    rankingsRecyclerView.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsLayoutFailure.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsListLayoutLoading.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsSelectedChipsLoadingStub.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsChipWorldRegion.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsChipOptionType.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsDivider.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    rankingsDialog.ifTrue(views::doesNotContain) { animateInvisibleIfNeeded() }
    views.forEach { it.animateVisibleIfNeeded() }
  }
  
  private fun toggleClickableItems(enable: Boolean) {
    rankingsHeaderLayout.asHeader.isScrollable = enable
    rankingsFabFilter.isEnabled = enable
    rankingsRecyclerView.isEnabled = enable
    setClickable(enable, rankingsIconDrawer, rankingsFabFilter,
      rankingsChipOptionType, rankingsChipWorldRegion)
  }
  
  private fun setupAccordingToStatusBarHeight() {
    val statusBarHeight = requireContext().statusBarHeight
    rankingsHeaderGradientView.layoutParams.height += statusBarHeight
    (rankingsIconDrawer.layoutParams as MarginLayoutParams).topMargin += statusBarHeight
    rankingsDialog.setPadding(0, requireContext().statusBarHeight, 0, 0)
  }
  
  private fun setupClickListeners() {
    rankingsIconDrawer.setOnClickListener { hostActivity.openDrawer() }
    rankingsRetryButton.setOnClickListener { viewModel.retryLoadingData() }
    onClick(rankingsFabFilter, rankingsChipOptionType, rankingsChipWorldRegion) {
      viewModel.onFilterDialogShow()
    }
    rankingsFilterDialog.asBottomSheet.onShow = { viewModel.onFilterDialogShow() }
    rankingsFilterDialog.asBottomSheet.onHide = { viewModel.onFilterDialogHide() }
    rankingsFilterDialogCross.onClick { viewModel.onFilterDialogHide() }
    rankingsCountryFullInfoDialogIconBack.onClick { viewModel.onCountryFullInfoDialogBackClicked() }
  }
  
  private fun setupBehavior() {
    rankingsHeaderLayout.asHeader.calculateSlideRangeCoefficient = l@{
      val chipMargin = dimen(R.dimen.rankings_header_chip_margin).toInt()
      val dividerMargin = dimen(R.dimen.rankings_divider_m_top).toInt()
      val dividerHeight = dimen(R.dimen.divider_height).toInt()
      val headerHeight = dimen(
        R.dimen.rankings_header_height).toInt() + requireContext().statusBarHeight
      val textChip = view<Chip>(WrapContent, WrapContent) {
        margins(top = chipMargin, bottom = chipMargin)
        text = context.getString(R.string.text_worldwide)
      }
      textChip.measure(unspecified(), unspecified())
      val height = textChip.heightWithMargins() + dividerHeight + dividerMargin
      return@l 1 - (height.toFloat() / (headerHeight + height))
    }
  }
  
  private fun setupChips() {
    rankingsChipWorldRegion.isSelected = true
    rankingsChipOptionType.isSelected = true
  }
  
  private fun setupDrawables() {
    rankingsHeaderGradientView.background(GradientHeaderDrawable())
    rankingsListLayoutLoading.setLoadingDrawable(RankingsListLoadingStub())
    rankingsSelectedChipsLoadingStub.setLoadingDrawable(
      SelectedChipsLoadingStub(
        requireContext(), R.dimen.rankings_chip_text_size, R.dimen.rankings_header_chip_margin
      )
    )
  }
}