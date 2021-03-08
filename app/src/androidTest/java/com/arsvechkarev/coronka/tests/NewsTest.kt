package com.arsvechkarev.coronka.tests

import android.os.SystemClock.sleep
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.common.CoreDiComponent
import com.arsvechkarev.coronka.FakeNetworkAvailabilityNotifier
import com.arsvechkarev.coronka.RetryCountWebApiFactory
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screen
import com.arsvechkarev.coronka.screens.DrawerScreen
import com.arsvechkarev.coronka.screens.NewsScreen
import com.arsvechkarev.coronka.screens.StatsScreen
import core.RxConfigurator
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class NewsTest {
  
  @get:Rule
  val rule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
    
    override fun beforeActivityLaunched() {
      val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
      val webApiFactory = RetryCountWebApiFactory(1)
      CoreDiComponent.initCustom(webApiFactory, FakeNetworkAvailabilityNotifier, applicationContext)
      RxConfigurator.configureRetryCount(0)
      RxConfigurator.configureNetworkDelay(0)
    }
  }
  
  @Test
  fun test_showing_error_and_then_normal_news() {
    
    screen<StatsScreen>().iconDrawer.click()
    
    screen<DrawerScreen>().textNews.click()
    
    sleep(500)
    
    onScreen<NewsScreen> {
      recyclerView.isNotDisplayed()
      errorLayout.isDisplayed()
      
      buttonRetry.click()
      
      sleep(500)
      
      recyclerView.isDisplayed()
      errorLayout.isNotDisplayed()
    }
  }
  
  @Test
  fun test_notifying_about_network() {
    
    screen<StatsScreen>().iconDrawer.click()
    
    screen<DrawerScreen>().textNews.click()
    
    sleep(500)
    
    onScreen<NewsScreen> {
      recyclerView.isNotDisplayed()
      errorLayout.isDisplayed()
      
      FakeNetworkAvailabilityNotifier.notifyNetworkAvailable()
      
      sleep(1000)
      
      recyclerView.isDisplayed()
      errorLayout.isNotDisplayed()
    }
  }
}