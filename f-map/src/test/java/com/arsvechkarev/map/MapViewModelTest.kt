package com.arsvechkarev.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arsvechkarev.map.presentation.FoundCountry
import com.arsvechkarev.map.presentation.LoadedCountries
import com.arsvechkarev.map.presentation.MapViewModel
import com.arsvechkarev.test.FakeCountries
import com.arsvechkarev.test.FakeCountriesMetaInfoDataSource
import com.arsvechkarev.test.FakeLocationsMap
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import com.arsvechkarev.test.FakeScreenStateObserver
import com.arsvechkarev.test.FakeTotalInfo
import com.arsvechkarev.test.FakeTotalInfoDataSource
import com.arsvechkarev.test.currentState
import com.arsvechkarev.test.hasStateAtPosition
import com.arsvechkarev.test.hasStatesCount
import com.arsvechkarev.test.state
import config.RxConfigurator
import core.Failure
import core.Failure.FailureReason.NO_CONNECTION
import core.Loading
import core.transformers.MapTransformer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class MapViewModelTest {
  
  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()
  
  private val maxRetryCount = 3
  
  @Before
  fun setUp() {
    RxConfigurator.configureNetworkDelay(0)
    RxConfigurator.configureRetryCount(maxRetryCount.toLong())
  }
  
  @Test
  fun `Basic flow`() {
    val viewModel = createViewModel()
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStatesCount(2)
      hasStateAtPosition<LoadedCountries>(1)
      val expected = MapTransformer.transformResult(Pair(FakeTotalInfo, FakeLocationsMap))
      assertEquals(expected, currentState<LoadedCountries>().iso2ToCountryMap)
    }
  }
  
  @Test
  fun `Error handling`() {
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = TimeoutException()
    )
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStatesCount(2)
      hasStateAtPosition<Loading>(0)
      assertEquals(Failure.FailureReason.TIMEOUT, currentState<Failure>().reason)
    }
  }
  
  @Test
  fun `Testing retry`() {
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException()
    )
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData() // Initial loading
    viewModel.startLoadingData() // Retry
    
    with(observer) {
      hasStatesCount(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Failure>(1)
      hasStateAtPosition<Loading>(2)
      hasStateAtPosition<LoadedCountries>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val worldCasesInfo = currentState<LoadedCountries>().iso2ToCountryMap
      val expected = MapTransformer.transformResult(Pair(FakeTotalInfo, FakeLocationsMap))
      assertEquals(expected, worldCasesInfo)
    }
  }
  
  @Test
  fun `Testing network availability callback`() {
    val notifier = FakeNetworkAvailabilityNotifier()
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException(),
      notifier
    )
    val observer = createObserver()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    notifier.notifyNetworkAvailable()
    
    with(observer) {
      hasStatesCount(4)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<Failure>(1)
      hasStateAtPosition<Loading>(2)
      hasStateAtPosition<LoadedCountries>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val worldCasesInfo = currentState<LoadedCountries>().iso2ToCountryMap
      val expected = MapTransformer.transformResult(Pair(FakeTotalInfo, FakeLocationsMap))
      assertEquals(expected, worldCasesInfo)
    }
  }
  
  @Test
  fun `Clicking on map`() {
    val viewModel = createViewModel()
    val observer = createObserver()
    val countryToClick = FakeCountries[0]
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.showCountryInfo(countryToClick)
    
    with(observer) {
      hasStatesCount(3)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedCountries>(1)
      hasStateAtPosition<FoundCountry>(2)
      val expected = MapTransformer.transformResult(Pair(FakeTotalInfo, FakeLocationsMap))
      assertEquals(expected, state<FoundCountry>(2).iso2ToCountryMap)
      assertEquals(countryToClick, state<FoundCountry>(2).country)
    }
  }
  
  private fun createViewModel(
    totalRetryCount: Int = 0,
    error: Throwable = Throwable(),
    notifier: FakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  ): MapViewModel {
    val totalInfoDataSource = FakeTotalInfoDataSource(
      totalRetryCount = totalRetryCount,
      errorFactory = { error }
    )
    return MapViewModel(
      totalInfoDataSource,
      FakeCountriesMetaInfoDataSource(),
      notifier,
      FakeSchedulers
    )
  }
  
  private fun createObserver(): FakeScreenStateObserver {
    return FakeScreenStateObserver()
  }
}