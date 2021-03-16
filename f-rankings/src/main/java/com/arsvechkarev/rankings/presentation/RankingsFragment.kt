package com.arsvechkarev.rankings.presentation

import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewGroup.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.rankings.R
import com.arsvechkarev.rankings.di.RankingsModuleInjector
import com.arsvechkarev.viewdsl.Size.Companion.WrapContent
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.background
import com.arsvechkarev.viewdsl.dimen
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.heightWithMargins
import com.arsvechkarev.viewdsl.margins
import com.arsvechkarev.viewdsl.onClick
import com.arsvechkarev.viewdsl.setClickable
import com.arsvechkarev.viewdsl.statusBarHeight
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.unspecified
import com.arsvechkarev.viewdsl.view
import com.arsvechkarev.viewdsl.visible
import com.arsvechkarev.views.Chip
import com.arsvechkarev.views.behaviors.BottomSheetBehavior.Companion.asBottomSheet
import com.arsvechkarev.views.behaviors.HeaderBehavior.Companion.asHeader
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.asLoadingStub
import com.arsvechkarev.views.drawables.BaseLoadingStub.Companion.setLoadingDrawable
import com.arsvechkarev.views.drawables.GradientHeaderDrawable
import com.arsvechkarev.views.drawables.RankingsListLoadingStub
import com.arsvechkarev.views.drawables.SelectedChipsLoadingStub
import core.BaseFragment
import core.BaseScreenState
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.FailureReason.UNKNOWN
import core.Loading
import core.extenstions.toFormattedDecimalNumber
import core.extenstions.toFormattedNumber
import core.hostActivity
import kotlinx.android.synthetic.main.fragment_rankings.chipAfrica
import kotlinx.android.synthetic.main.fragment_rankings.chipAsia
import kotlinx.android.synthetic.main.fragment_rankings.chipConfirmed
import kotlinx.android.synthetic.main.fragment_rankings.chipDeathRate
import kotlinx.android.synthetic.main.fragment_rankings.chipDeaths
import kotlinx.android.synthetic.main.fragment_rankings.chipEurope
import kotlinx.android.synthetic.main.fragment_rankings.chipNorthAmerica
import kotlinx.android.synthetic.main.fragment_rankings.chipOceania
import kotlinx.android.synthetic.main.fragment_rankings.chipPercentByCountry
import kotlinx.android.synthetic.main.fragment_rankings.chipRecovered
import kotlinx.android.synthetic.main.fragment_rankings.chipSouthAmerica
import kotlinx.android.synthetic.main.fragment_rankings.chipWorldwide
import kotlinx.android.synthetic.main.fragment_rankings.rankingsBottomSheet
import kotlinx.android.synthetic.main.fragment_rankings.rankingsBottomSheetCross
import kotlinx.android.synthetic.main.fragment_rankings.rankingsChipOptionType
import kotlinx.android.synthetic.main.fragment_rankings.rankingsChipWorldRegion
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialog
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogGeneralView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogIconBack
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDialogTitle
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDivider
import kotlinx.android.synthetic.main.fragment_rankings.rankingsErrorLayout
import kotlinx.android.synthetic.main.fragment_rankings.rankingsErrorMessage
import kotlinx.android.synthetic.main.fragment_rankings.rankingsFabFilter
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderGradientView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderLayout
import kotlinx.android.synthetic.main.fragment_rankings.rankingsIconDrawer
import kotlinx.android.synthetic.main.fragment_rankings.rankingsImageFailure
import kotlinx.android.synthetic.main.fragment_rankings.rankingsListLoadingStub
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRecyclerView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRetryButton
import kotlinx.android.synthetic.main.fragment_rankings.rankingsSelectedChipsLoadingStub
import kotlinx.android.synthetic.main.fragment_rankings.rankingsTextDeathRate
import kotlinx.android.synthetic.main.fragment_rankings.rankingsTextNewConfirmed
import kotlinx.android.synthetic.main.fragment_rankings.rankingsTextNewDeaths
import kotlinx.android.synthetic.main.fragment_rankings.rankingsTextPercentInCountry
import timber.log.Timber

class RankingsFragment : BaseFragment(R.layout.fragment_rankings) {
  
  private lateinit var chipHelper: ChipHelper
  private var viewModel: RankingsViewModel? = null
  private val adapter = RankingsModuleInjector.provideAdapter { country ->
    viewModel?.onCountryClicked(country)
  }
  
  override fun onInit() {
    viewModel = RankingsModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(::handleState))
      model.startLoadingData()
    }
    val statusBarHeight = requireContext().statusBarHeight
    rankingsHeaderGradientView.layoutParams.height += statusBarHeight
    (rankingsIconDrawer.layoutParams as MarginLayoutParams).topMargin += statusBarHeight
    rankingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    rankingsRecyclerView.adapter = adapter
    setupClickListeners()
    setupBehavior()
    setupChips()
    setupDrawables()
    rankingsDialog.setPadding(0, requireContext().statusBarHeight, 0, 0)
  }
  
  override fun onDrawerOpened() = toggleItems(false)
  
  override fun onDrawerClosed() = toggleItems(true)
  
  override fun onOrientationBecameLandscape() {
    rankingsImageFailure.gone()
  }
  
  override fun onOrientationBecamePortrait() {
    rankingsImageFailure.visible()
  }
  
  override fun allowBackPress(): Boolean {
    if (rankingsDialog.visibility == VISIBLE) {
      goBackFromCountryInfo()
      return false
    }
    return true
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> renderLoading()
      is LoadedCountries -> renderLoaded(state)
      is FilteredCountries -> renderFiltered(state)
      is ShowCountryInfo -> renderShowCountryInfo(state)
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderLoading() {
    rankingsFabFilter.isClickable = false
    animateInvisible(rankingsErrorLayout, rankingsChipWorldRegion, rankingsChipOptionType,
      rankingsDivider, rankingsRecyclerView)
    rankingsListLoadingStub.asLoadingStub.start()
    rankingsSelectedChipsLoadingStub.asLoadingStub.start()
    animateVisible(rankingsListLoadingStub, rankingsSelectedChipsLoadingStub)
  }
  
  private fun renderLoaded(state: LoadedCountries) {
    rankingsFabFilter.isClickable = true
    stopLoadingStubs()
    animateVisible(rankingsRecyclerView, rankingsChipWorldRegion,
      rankingsChipOptionType, rankingsDivider)
    adapter.submitList(state.list)
  }
  
  private fun renderFiltered(state: FilteredCountries) {
    rankingsFabFilter.isClickable = true
    rankingsHeaderLayout.asHeader.animateScrollToTop(andThen = {
      adapter.submitList(state.list)
    })
  }
  
  private fun renderShowCountryInfo(state: ShowCountryInfo) {
    val country = state.country
    rankingsDialogTitle.text(country.name)
    rankingsDialogGeneralView.updateData(
      country.confirmed,
      country.recovered,
      country.deaths,
    )
    rankingsHeaderLayout.asHeader.isScrollable = false
    rankingsRecyclerView.isEnabled = false
    setClickable(false, rankingsIconDrawer, rankingsFabFilter,
      rankingsChipOptionType, rankingsChipWorldRegion)
    rankingsTextNewConfirmed.text(country.newConfirmed.toFormattedNumber())
    rankingsTextNewDeaths.text(country.newDeaths.toFormattedNumber())
    rankingsTextDeathRate.text(state.deathRate.toFormattedDecimalNumber())
    rankingsTextPercentInCountry.text(state.percentInCountry.toFormattedDecimalNumber())
    rankingsDialog.animateVisible()
  }
  
  private fun renderFailure(state: Failure) {
    Timber.d(state.throwable)
    rankingsFabFilter.isClickable = false
    stopLoadingStubs()
    rankingsErrorLayout.animateVisible()
    rankingsErrorMessage.text(state.reason.getStringRes())
    when (state.reason) {
      TIMEOUT, UNKNOWN -> rankingsImageFailure.setImageResource(R.drawable.image_unknown_error)
      NO_CONNECTION -> rankingsImageFailure.setImageResource(R.drawable.image_no_connection)
    }
  }
  
  private fun toggleItems(enable: Boolean) {
    rankingsHeaderLayout.asHeader.isScrollable = enable
    rankingsFabFilter.isEnabled = enable
    rankingsRecyclerView.isEnabled = enable
  }
  
  private fun setupClickListeners() {
    rankingsIconDrawer.setOnClickListener { hostActivity.openDrawer() }
    rankingsRetryButton.setOnClickListener { viewModel!!.startLoadingData() }
    onClick(rankingsFabFilter, rankingsChipOptionType, rankingsChipWorldRegion) {
      rankingsBottomSheet.asBottomSheet.show()
    }
    rankingsBottomSheet.asBottomSheet.onShow = {
      toggleItems(false)
      hostActivity.disableTouchesOnDrawer()
    }
    rankingsBottomSheet.asBottomSheet.onHide = {
      toggleItems(true)
      hostActivity.enableTouchesOnDrawer()
    }
    rankingsBottomSheetCross.onClick { rankingsBottomSheet.asBottomSheet.hide() }
    rankingsDialogIconBack.onClick { goBackFromCountryInfo() }
  }
  
  private fun stopLoadingStubs() {
    animateInvisible(rankingsListLoadingStub, rankingsSelectedChipsLoadingStub,
      andThen = {
        rankingsListLoadingStub.asLoadingStub.stop()
        rankingsSelectedChipsLoadingStub.asLoadingStub.stop()
      }
    )
  }
  
  private fun setupBehavior() {
    rankingsHeaderLayout.asHeader.apply {
      calculateSlideRangeCoefficient = l@{
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
  }
  
  private fun setupChips() {
    rankingsChipWorldRegion.isSelected = true
    rankingsChipOptionType.isSelected = true
    chipWorldwide.isSelected = true
    chipConfirmed.isSelected = true
    val groupWorldRegions = ChipGroup(chipWorldwide, chipEurope, chipAsia,
      chipAfrica, chipNorthAmerica, chipOceania, chipSouthAmerica)
    val groupOptionTypes = ChipGroup(chipConfirmed, chipRecovered, chipDeaths,
      chipPercentByCountry, chipDeathRate)
    chipHelper = ChipHelper(groupOptionTypes, groupWorldRegions,
      onNewChipSelected = { worldRegion, optionType ->
        viewModel!!.filter(worldRegion, optionType)
      },
      onOptionTypeChipSelected = { chip ->
        rankingsChipOptionType.text = chip.text
        rankingsChipOptionType.colorFill = chip.colorFill
      },
      onWorldRegionChipSelected = { chip ->
        rankingsChipWorldRegion.text = chip.text
      })
  }
  
  private fun goBackFromCountryInfo() {
    rankingsHeaderLayout.asHeader.isScrollable = true
    rankingsFabFilter.isClickable = true
    rankingsChipOptionType.isClickable = true
    rankingsChipWorldRegion.isClickable = true
    rankingsDialog.animateInvisible(andThen = {
      rankingsRecyclerView.isEnabled = true
    })
  }
  
  private fun setupDrawables() {
    rankingsHeaderGradientView.background(GradientHeaderDrawable())
    rankingsListLoadingStub.setLoadingDrawable(RankingsListLoadingStub())
    rankingsSelectedChipsLoadingStub.setLoadingDrawable(
      SelectedChipsLoadingStub(
        requireContext(), R.dimen.rankings_chip_text_size, R.dimen.rankings_header_chip_margin
      )
    )
  }
}