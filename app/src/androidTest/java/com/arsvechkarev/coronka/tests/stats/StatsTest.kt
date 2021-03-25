package com.arsvechkarev.coronka.tests.stats

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import base.extensions.f
import base.extensions.toFormattedNumber
import base.extensions.toFormattedTextLabelDate
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.coronka.configureDurationsAndDelaysForTests
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screens.StatsScreen
import com.arsvechkarev.stats.di.StatsModule
import core.di.ModuleInterceptorManager
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class StatsTest {
  
  companion object {
    
    @BeforeClass
    @JvmStatic
    fun setup() {
      configureDurationsAndDelaysForTests()
      ModuleInterceptorManager.addInterceptorForModule<StatsModule> { FakeStatsModule }
    }
  }
  
  @get:Rule
  val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)
  
  @Test
  fun test_displaying_stats() {
    val generalInfo = DataProvider.getGeneralInfo()
    val dailyCases = DataProvider.getDailyCases()
    val newCases = DataProvider.getNewCases()
    
    onScreen<StatsScreen> {
      iconDrawer {
        isVisible()
      }
      generalStatsView {
        hasGeneralInfo(generalInfo)
      }
      
      val lastIndex = dailyCases.lastIndex
      val lastDailyCase = dailyCases.last()
      totalCasesLabel {
        hasDateText(lastDailyCase.date.toFormattedTextLabelDate())
        hasNumberText(lastDailyCase.cases.toFormattedNumber())
      }
      totalCasesChart {
        hasEntryForIndex(
          lastIndex,
          lastIndex.f,
          lastDailyCase.cases.f,
        )
      }
      val newCaseNumber = newCases.last().cases
      newCasesLabel {
        hasDateText(lastDailyCase.date.toFormattedTextLabelDate())
        hasNumberText(newCaseNumber.toFormattedNumber())
      }
      newCasesChart {
        hasEntryForIndex(lastIndex, lastIndex.f, newCaseNumber.f)
      }
    }
  }
}