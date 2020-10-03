package com.arsvechkarev.rankings.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsvechkarev.rankings.R
import com.arsvechkarev.rankings.di.RankingsDiInjector
import com.arsvechkarev.rankings.list.RankingsAdapter
import com.arsvechkarev.rankings.presentation.RankingsScreenState.Success
import com.arsvechkarev.views.behaviors.BottomSheetBehavior
import com.arsvechkarev.views.behaviors.HeaderBehavior
import com.arsvechkarev.views.drawables.GradientHeaderDrawable.Companion.createGradientHeaderDrawable
import core.extenstions.getBehavior
import core.extenstions.heightWithMargins
import core.state.BaseScreenState
import core.state.Loading
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
import kotlinx.android.synthetic.main.fragment_rankings.rankingsDivider
import kotlinx.android.synthetic.main.fragment_rankings.rankingsFabFilter
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderGradientView
import kotlinx.android.synthetic.main.fragment_rankings.rankingsHeaderLayout
import kotlinx.android.synthetic.main.fragment_rankings.rankingsRecyclerView

class RankingsFragment : Fragment(R.layout.fragment_rankings) {
  
  private lateinit var viewModel: RankingsViewModel
  private lateinit var chipHelper: ChipHelper
  
  private val adapter = RankingsAdapter()
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel = RankingsDiInjector.provideViewModel(this)
    viewModel.startInitialLoading()
    viewModel.state.observe(this, Observer(::handleState))
    rankingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    rankingsRecyclerView.adapter = adapter
    rankingsFabFilter.setOnClickListener {
      rankingsBottomSheet.getBehavior<BottomSheetBehavior<*>>().show()
    }
    rankingsBottomSheetCross.setOnClickListener {
      rankingsBottomSheet.getBehavior<BottomSheetBehavior<*>>().hide()
    }
    rankingsHeaderGradientView.background =
        createGradientHeaderDrawable(R.dimen.rankings_header_curve_size)
    setupBehavior()
    setupChips()
  }
  
  private fun handleState(state: BaseScreenState) {
    when (state) {
      is Loading -> {
      }
      is Success -> {
        adapter.submitList(state.list)
      }
    }
  }
  
  private fun setupBehavior() {
    rankingsHeaderLayout.getBehavior<HeaderBehavior<*>>().apply {
      reactToTouches = false
      rankingsHeaderLayout.post {
        val height = calculateSelectedChipsHeight()
        slideRangeCoefficient = height.toFloat() / rankingsHeaderLayout.height
      }
    }
  }
  
  private fun calculateSelectedChipsHeight(): Int {
    return rankingsChipOptionType.heightWithMargins() + rankingsDivider.heightWithMargins()
  }
  
  private fun setupChips() {
    rankingsChipWorldRegion.isActive = true
    rankingsChipOptionType.isActive = true
    chipWorldwide.isActive = true
    chipConfirmed.isActive = true
    val groupWorldRegions = ChipGroup(chipWorldwide, chipAsia, chipEurope,
      chipAfrica, chipOceania, chipNorthAmerica, chipSouthAmerica)
    val groupOptionTypes = ChipGroup(chipConfirmed, chipRecovered, chipDeaths,
      chipPercentByCountry, chipDeathRate)
    chipHelper = ChipHelper(groupOptionTypes, groupWorldRegions,
      onNewChipSelected = { optionType, worldRegion ->
        viewModel.filter(optionType, worldRegion)
      },
      onOptionTypeChipSelected = { chip ->
        rankingsChipOptionType.text = chip.text
        rankingsChipOptionType.colorFill = chip.colorFill
      },
      onWorldRegionChipSelected = { chip ->
        rankingsChipWorldRegion.text = chip.text
      })
  }
}