package com.arsvechkarev.map.presentation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapModuleInjector
import com.arsvechkarev.map.uils.BaseMapFragment
import com.arsvechkarev.map.uils.MapHelper
import com.arsvechkarev.views.behaviors.BottomSheetBehavior.Companion.asBottomSheet
import core.extenstions.animateInvisible
import core.extenstions.animateVisible
import core.extenstions.invisible
import core.extenstions.visible
import core.hostActivity
import core.model.Country
import core.state.BaseScreenState
import core.state.Failure
import core.state.Failure.FailureReason.NO_CONNECTION
import core.state.Failure.FailureReason.TIMEOUT
import core.state.Failure.FailureReason.UNKNOWN
import core.state.Loading
import kotlinx.android.synthetic.main.fragment_map.mapEarthView
import kotlinx.android.synthetic.main.fragment_map.mapIconDrawer
import kotlinx.android.synthetic.main.fragment_map.mapLayoutCountryInfo
import kotlinx.android.synthetic.main.fragment_map.mapLayoutFailure
import kotlinx.android.synthetic.main.fragment_map.mapLayoutLoading
import kotlinx.android.synthetic.main.fragment_map.mapLayoutNoConnection
import kotlinx.android.synthetic.main.fragment_map.mapLayoutUnknownError
import kotlinx.android.synthetic.main.fragment_map.mapStatsView
import kotlinx.android.synthetic.main.fragment_map.mapTextFailureReason
import kotlinx.android.synthetic.main.fragment_map.mapTextRetry
import kotlinx.android.synthetic.main.fragment_map.mapTextRetryUnknown
import kotlinx.android.synthetic.main.fragment_map.mapTextViewCountryName

class MapFragment : BaseMapFragment(R.layout.fragment_map) {
  
  private lateinit var mapHelper: MapHelper
  private var viewModel: MapViewModel? = null
  
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    mapHelper = MapHelper(requireContext(), mapView, ::onCountrySelected)
    viewModel = MapModuleInjector.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(this::handleStateChanged))
      model.startLoadingData()
    }
    setupClickListeners()
  }
  
  override fun onAppearedOnScreen() {
    hostActivity.disableTouchesOnDrawer()
  }
  
  override fun onNetworkAvailable() {
    val viewModel = viewModel ?: return
    val value = viewModel.state.value ?: return
    if (value !is Loading && value !is FoundCountry && value !is LoadedCountries) {
      viewModel.startLoadingData()
    }
  }
  
  override fun onDrawerClosed() {
    hostActivity.disableTouchesOnDrawer()
  }
  
  private fun handleStateChanged(state: BaseScreenState) {
    when (state) {
      is Loading -> renderLoading()
      is LoadedCountries -> renderLoadedFromNetwork(state)
      is FoundCountry -> renderFoundCountry(state)
      is Failure -> renderFailure(state)
    }
  }
  
  private fun renderLoading() {
    mapHelper.toggleMap(enable = false)
    mapLayoutFailure.animateInvisible()
    mapLayoutLoading.animateVisible()
  }
  
  private fun renderLoadedFromNetwork(state: LoadedCountries) {
    mapLayoutLoading.animateInvisible()
    mapHelper.toggleMap(enable = true)
    mapHelper.drawCountries(state.iso2ToCountryMap)
  }
  
  private fun renderFoundCountry(state: FoundCountry) {
    mapHelper.toggleMap(enable = true)
    mapLayoutCountryInfo.asBottomSheet.show()
    mapTextViewCountryName.text = state.country.name
    mapStatsView.updateNumbers(
      state.country.confirmed,
      state.country.recovered,
      state.country.deaths
    )
  }
  
  private fun renderFailure(state: Failure) {
    mapHelper.toggleMap(enable = false)
    mapLayoutUnknownError.invisible()
    mapLayoutNoConnection.invisible()
    when (state.reason) {
      NO_CONNECTION -> {
        mapTextFailureReason.text = getString(R.string.text_no_connection)
        mapLayoutNoConnection.visible()
        mapEarthView.animateWifi()
      }
      TIMEOUT -> {
        mapTextFailureReason.text = getString(R.string.text_timeout)
        mapLayoutNoConnection.visible()
        mapEarthView.animateWifi()
      }
      UNKNOWN -> mapLayoutUnknownError.visible()
    }
    mapTextRetry.isClickable = false
    mapLayoutFailure.animateVisible(andThen = { mapTextRetry.isClickable = true })
    mapLayoutLoading.animateInvisible()
  }
  
  private fun onCountrySelected(country: Country) {
    viewModel!!.showCountryInfo(country)
  }
  
  private fun setupClickListeners() {
    mapTextRetry.setOnClickListener { viewModel!!.startLoadingData() }
    mapTextRetryUnknown.setOnClickListener { viewModel!!.startLoadingData() }
    mapIconDrawer.setOnClickListener {
      hostActivity.openDrawer()
      hostActivity.enableTouchesOnDrawer()
    }
  }
}