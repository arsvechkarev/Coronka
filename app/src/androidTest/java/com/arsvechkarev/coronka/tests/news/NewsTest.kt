package com.arsvechkarev.coronka.tests.news

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.coronka.clickAndWaitForIdle
import com.arsvechkarev.coronka.configureDurationsAndDelaysForTests
import com.arsvechkarev.coronka.fakes.FakeCoreModule
import com.arsvechkarev.coronka.idleMainThread
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screen
import com.arsvechkarev.coronka.screens.DrawerScreen
import com.arsvechkarev.coronka.screens.NewsScreen
import com.arsvechkarev.coronka.screens.StatsScreen
import com.arsvechkarev.news.di.NewsModule
import com.arsvechkarev.test.FakeNetworkAvailabilityNotifier
import core.di.CoreComponent
import core.di.ModuleInterceptorManager.addInterceptorForModule
import core.model.mappers.NewsItemsMapper
import coreimpl.EnglishDateTimeFormatter
import coreimpl.ThreeTenAbpDateTimeCreator
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
class NewsTest {
  
  companion object {
    
    val fakeNetworkAvailabilityNotifier = FakeNetworkAvailabilityNotifier()
    
    @BeforeClass
    @JvmStatic
    fun setup() {
      configureDurationsAndDelaysForTests()
      val coreModule = object : FakeCoreModule() {
        override val networkAvailabilityNotifier = fakeNetworkAvailabilityNotifier
      }
      CoreComponent.initialize(coreModule)
      addInterceptorForModule<NewsModule> lb@{
        val context = InstrumentationRegistry.getInstrumentation().context
        val dateTimeFormatter = EnglishDateTimeFormatter(context, ThreeTenAbpDateTimeCreator)
        return@lb FakeNewsModule(NewsItemsMapper(dateTimeFormatter))
      }
    }
  }
  
  @get:Rule
  val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)
  
  @Test
  fun test_showing_error_and_then_normal_news() {
    
    screen<StatsScreen>().iconDrawer.clickAndWaitForIdle()
    
    screen<DrawerScreen>().textNews.clickAndWaitForIdle()
    
    onScreen<NewsScreen> {
      recyclerView.isNotDisplayed()
      errorLayout.isDisplayed()
      
      buttonRetry.click()
      
      recyclerView.isDisplayed()
      errorLayout.isNotDisplayed()
    }
  }
  
  @Test
  fun test_notifying_about_network() {
  
    screen<StatsScreen>().iconDrawer.clickAndWaitForIdle()
  
    screen<DrawerScreen>().textNews.clickAndWaitForIdle()
    
    onScreen<NewsScreen> {
      recyclerView.isNotDisplayed()
      errorLayout.isDisplayed()
  
      fakeNetworkAvailabilityNotifier.notifyNetworkAvailable()
  
      idleMainThread()
      sleep(1500) // Wait for list to be animated
  
      recyclerView.isDisplayed()
      errorLayout.isNotDisplayed()
    }
  }
}