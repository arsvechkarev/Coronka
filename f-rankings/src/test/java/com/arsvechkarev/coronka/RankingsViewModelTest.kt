package com.arsvechkarev.coronka

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arsvechkarev.rankings.presentation.FilteredCountries
import com.arsvechkarev.rankings.presentation.LoadedCountries
import com.arsvechkarev.rankings.presentation.RankingsViewModel
import com.arsvechkarev.rankings.presentation.RankingsViewModel.Companion.DefaultOptionType
import com.arsvechkarev.rankings.presentation.RankingsViewModel.Companion.DefaultWorldRegion
import com.arsvechkarev.rankings.presentation.ShowCountryInfo
import com.arsvechkarev.test.FakeCountries
import com.arsvechkarev.test.FakeCountriesMetaInfoDataSource
import com.arsvechkarev.test.FakeMetaInfoMap
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import com.arsvechkarev.test.FakeScreenStateObserver
import com.arsvechkarev.test.FakeTotalInfoDataSource
import com.arsvechkarev.test.currentState
import com.arsvechkarev.test.hasStateAtPosition
import com.arsvechkarev.test.hasStatesCount
import com.arsvechkarev.test.state
import config.RxConfigurator
import core.CountriesFilterer
import core.CountriesFiltererImpl
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.Loading
import core.model.DisplayableCountry
import core.model.OptionType.RECOVERED
import core.model.WorldRegion.EUROPE
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class RankingsViewModelTest {
  
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
    val countriesFilterer = CountriesFiltererImpl()
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    
    with(observer) {
      hasStatesCount(2)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedCountries>(1)
      val expectedCountries = countriesFilterer.filterInitial(FakeCountries, FakeMetaInfoMap,
        DefaultWorldRegion, DefaultOptionType)
      val loadedCountries = currentState<LoadedCountries>().list
      assertArrayEquals(expectedCountries.toTypedArray(), loadedCountries.toTypedArray())
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
      assertEquals(TIMEOUT, currentState<Failure>().reason)
    }
  }
  
  @Test
  fun `Testing retry`() {
    val countriesFilterer = CountriesFiltererImpl()
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException()
    )
    val observer = createObserver()
    
    val initialFilteredList = countriesFilterer.filterInitial(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
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
      val list = currentState<LoadedCountries>().list
      assertArrayEquals(initialFilteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  
  @Test
  fun `Testing network availability callback`() {
    val countriesFilterer = CountriesFiltererImpl()
    val notifier = FakeNetworkAvailabilityNotifier()
    val viewModel = createViewModel(
      totalRetryCount = maxRetryCount + 1,
      error = UnknownHostException(),
      notifier = notifier
    )
    val observer = createObserver()
    
    val initialFilteredList = countriesFilterer.filterInitial(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
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
      val list = currentState<LoadedCountries>().list
      assertArrayEquals(initialFilteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  @Test
  fun `Test filtering`() {
    val countriesFilterer = CountriesFiltererImpl()
    val viewModel = createViewModel()
    val observer = createObserver()
    val worldRegion = EUROPE
    val optionType = RECOVERED
    
    val filteredList = countriesFilterer.filterInitial(
      FakeCountries, FakeMetaInfoMap, worldRegion, optionType
    )
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.filter(worldRegion, optionType)
    
    with(observer) {
      hasStatesCount(3)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedCountries>(1)
      hasStateAtPosition<FilteredCountries>(2)
      val list = currentState<FilteredCountries>().list
      assertArrayEquals(filteredList.toTypedArray(), list.toTypedArray())
    }
  }
  
  @Test
  fun `Test showing country`() {
    val countriesFilterer = CountriesFiltererImpl()
    val viewModel = createViewModel()
    val observer = createObserver()
    
    val filteredList = countriesFilterer.filterInitial(
      FakeCountries, FakeMetaInfoMap, DefaultWorldRegion, DefaultOptionType
    )
    val countryIndex = 5
    val countryToClick = filteredList[countryIndex] as DisplayableCountry
    
    viewModel.state.observeForever(observer)
    viewModel.startLoadingData()
    viewModel.onCountryClicked(countryToClick)
    
    with(observer) {
      hasStatesCount(3)
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedCountries>(1)
      hasStateAtPosition<ShowCountryInfo>(2)
      currentState<ShowCountryInfo>().country == FakeCountries[countryIndex]
    }
  }
  
  private fun createViewModel(
    totalRetryCount: Int = 0,
    error: Throwable = Throwable(),
    countriesFilterer: CountriesFilterer = CountriesFiltererImpl(),
    notifier: FakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  ): RankingsViewModel {
    val totalInfoDataSource = FakeTotalInfoDataSource(totalRetryCount, errorFactory = { error })
    val countriesMetaInfoDataSource = FakeCountriesMetaInfoDataSource()
    return RankingsViewModel(
      totalInfoDataSource,
      countriesMetaInfoDataSource,
      countriesFilterer,
      notifier,
      FakeSchedulers
    )
  }
  
  private fun createObserver(): FakeScreenStateObserver {
    return FakeScreenStateObserver()
  }
}