package com.arsvechkarev.map.presentation

import android.view.ViewGroup.MarginLayoutParams
import androidx.lifecycle.Observer
import base.behaviors.BottomSheetBehavior.Companion.asBottomSheet
import base.extensions.getMessageRes
import base.hostActivity
import com.arsvechkarev.map.R
import com.arsvechkarev.map.di.MapComponent
import com.arsvechkarev.map.utils.BaseMapFragment
import com.arsvechkarev.map.utils.MapHelper
import com.arsvechkarev.viewdsl.animateInvisible
import com.arsvechkarev.viewdsl.animateVisible
import com.arsvechkarev.viewdsl.gone
import com.arsvechkarev.viewdsl.invisible
import com.arsvechkarev.viewdsl.statusBarHeight
import com.arsvechkarev.viewdsl.text
import com.arsvechkarev.viewdsl.visible
import core.BaseScreenState
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.FailureReason.UNKNOWN
import core.Loading
import core.di.CoreComponent.threader
import core.model.ui.CountryOnMapMetaInfo
import kotlinx.android.synthetic.main.fragment_map.mapEarthView
import kotlinx.android.synthetic.main.fragment_map.mapIconDrawer
import kotlinx.android.synthetic.main.fragment_map.mapImageFailure
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
  private lateinit var viewModel: MapViewModel
  
  override val enableTouchesOnDrawerWhenFragmentAppears: Boolean = false
  
  override fun onInit() {
    mapHelper = MapHelper(requireContext(), mapView, ::onCountrySelected, threader)
    viewModel = MapComponent.provideViewModel(this).also { model ->
      model.state.observe(this, Observer(this::handleStateChanged))
      model.startLoadingData()
    }
    setupViews()
    val statusBarHeight = requireContext().statusBarHeight
    (mapIconDrawer.layoutParams as MarginLayoutParams).topMargin += statusBarHeight
    hostActivity.disableTouchesOnDrawer()
  }
  
  override fun onHiddenChanged(hidden: Boolean) {
    if (!hidden) {
      hostActivity.disableTouchesOnDrawer()
    }
  }
  
  override fun onOrientationBecameLandscape() {
    mapEarthView.gone()
    mapImageFailure.gone()
  }
  
  override fun onOrientationBecamePortrait() {
    mapEarthView.visible()
    mapImageFailure.visible()
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
    mapView.invisible()
    mapLayoutFailure.animateInvisible()
    mapLayoutLoading.animateVisible()
  }
  
  private fun renderLoadedFromNetwork(state: LoadedCountries) {
    mapLayoutLoading.animateInvisible()
    mapView.animateVisible()
    mapHelper.toggleMap(enable = true)
    mapHelper.drawCountries(state.iso2ToCountryMapMetaInfo)
  }
  
  private fun renderFoundCountry(state: FoundCountry) {
    mapHelper.toggleMap(enable = true)
    mapTextViewCountryName.text = state.country.name
    mapStatsView.updateNumbers(
      state.country.confirmed,
      state.country.recovered,
      state.country.deaths
    )
    requireView().post { mapLayoutCountryInfo.asBottomSheet.show() }
  }
  
  private fun renderFailure(state: Failure) {
    mapView.invisible()
    mapHelper.toggleMap(enable = false)
    mapLayoutUnknownError.invisible()
    mapLayoutNoConnection.invisible()
    mapTextFailureReason.text(state.reason.getMessageRes())
    when (state.reason) {
      NO_CONNECTION, TIMEOUT -> {
        mapLayoutNoConnection.visible()
        mapEarthView.animateWifi()
      }
      UNKNOWN -> {
        mapLayoutUnknownError.visible()
      }
    }
    mapTextRetry.isClickable = false
    mapLayoutFailure.animateVisible(andThen = { mapTextRetry.isClickable = true })
    mapLayoutLoading.animateInvisible()
  }
  
  private fun onCountrySelected(countryOnMapMetaInfo: CountryOnMapMetaInfo) {
    viewModel.showCountryInfo(countryOnMapMetaInfo.id)
  }
  
  private fun setupViews() {
    mapLayoutCountryInfo.asBottomSheet.allowMultipleFingers = false
    mapTextRetry.setOnClickListener { viewModel.startLoadingData() }
    mapTextRetryUnknown.setOnClickListener { viewModel.startLoadingData() }
    mapIconDrawer.setOnClickListener {
      hostActivity.openDrawer()
      hostActivity.enableTouchesOnDrawer()
    }
  }
}