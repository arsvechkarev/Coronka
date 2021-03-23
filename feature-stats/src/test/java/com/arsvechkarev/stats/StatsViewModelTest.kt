package com.arsvechkarev.stats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arsvechkarev.common.domain.transformers.WorldCasesInfoTransformer.toNewDailyCases
import com.arsvechkarev.stats.presentation.LoadedWorldCasesInfo
import com.arsvechkarev.stats.presentation.StatsViewModel
import com.arsvechkarev.test.FakeDailyCases
import com.arsvechkarev.test.FakeGeneralInfo
import com.arsvechkarev.test.FakeGeneralInfoDataSource
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.test.FakeSchedulers
import com.arsvechkarev.test.FakeScreenStateObserver
import com.arsvechkarev.test.FakeWorldCasesInfoDataSource
import com.arsvechkarev.test.currentState
import com.arsvechkarev.test.hasCurrentState
import com.arsvechkarev.test.hasStateAtPosition
import com.arsvechkarev.test.hasStatesCount
import com.arsvechkarev.test.state
import config.RxConfigurator
import core.Failure
import core.FailureReason.NO_CONNECTION
import core.FailureReason.TIMEOUT
import core.Loading
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class StatsViewModelTest {
  
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
      hasStateAtPosition<Loading>(0)
      hasStateAtPosition<LoadedWorldCasesInfo>(1)
      hasCurrentState<LoadedWorldCasesInfo>()
      val worldCasesInfo = state<LoadedWorldCasesInfo>(1).worldCasesInfo
      assertEquals(FakeGeneralInfo, worldCasesInfo.generalInfo)
      assertEquals(FakeDailyCases, worldCasesInfo.totalDailyCases)
      assertEquals(toNewDailyCases(FakeDailyCases), worldCasesInfo.newDailyCases)
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
      hasStateAtPosition<LoadedWorldCasesInfo>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val worldCasesInfo = currentState<LoadedWorldCasesInfo>().worldCasesInfo
      assertEquals(FakeGeneralInfo, worldCasesInfo.generalInfo)
      assertEquals(FakeDailyCases, worldCasesInfo.totalDailyCases)
      assertEquals(toNewDailyCases(FakeDailyCases), worldCasesInfo.newDailyCases)
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
      hasStateAtPosition<LoadedWorldCasesInfo>(3)
      assertEquals(NO_CONNECTION, state<Failure>(1).reason)
      val worldCasesInfo = currentState<LoadedWorldCasesInfo>().worldCasesInfo
      assertEquals(FakeGeneralInfo, worldCasesInfo.generalInfo)
      assertEquals(FakeDailyCases, worldCasesInfo.totalDailyCases)
      assertEquals(toNewDailyCases(FakeDailyCases), worldCasesInfo.newDailyCases)
    }
  }
  
  private fun createViewModel(
    totalRetryCount: Int = 0,
    error: Throwable = Throwable(),
    notifier: FakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
  ): StatsViewModel {
    val generalInfoDataSource = FakeGeneralInfoDataSource(totalRetryCount, errorFactory = { error })
    return StatsViewModel(
      generalInfoDataSource,
      FakeWorldCasesInfoDataSource(),
      notifier,
      FakeSchedulers
    )
  }
  
  private fun createObserver(): FakeScreenStateObserver {
    return FakeScreenStateObserver()
  }
}