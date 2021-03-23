package com.arsvechkarev.coronka.tests

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import base.extensions.f
import base.extensions.toFormattedNumber
import base.extensions.toFormattedTextLabelDate
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.arsvechkarev.common.domain.GeneralInfoDataSource
import com.arsvechkarev.coronka.DataProvider
import com.arsvechkarev.coronka.configureDurationsAndDelaysForTests
import com.arsvechkarev.coronka.presentation.MainActivity
import com.arsvechkarev.coronka.screens.StatsScreen
import core.di.DependencyInterceptorManager
import core.model.GeneralInfo
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class StatsTest {
  
  @get:Rule
  val rule = object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
  
    override fun beforeActivityLaunched() {
      configureDurationsAndDelaysForTests()
      DependencyInterceptorManager.addDependencyInterceptor { dependencyClass: Class<*> ->
        if (dependencyClass == GeneralInfoDataSource::class.java) {
          object : GeneralInfoDataSource {
            override fun requestGeneralInfo(): Single<GeneralInfo> {
              return Single.just(DataProvider.getGeneralInfo())
            }
          }
        } else {
          null
        }
      }
    }
  }
  
  @Test
  fun test_displaying_stats() {
    val generalInfo = DataProvider.getGeneralInfo()
    val dailyCases = DataProvider.getDailyCases()
  
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
          lastIndex - 1,
          lastIndex.f,
          lastDailyCase.cases.f,
        )
      }
      val beforeLastDailyCase = dailyCases[lastIndex - 1]
      val newCaseNumber = lastDailyCase.cases - beforeLastDailyCase.cases
      newCasesLabel {
        hasDateText(lastDailyCase.date.toFormattedTextLabelDate())
        hasNumberText(newCaseNumber.toFormattedNumber())
      }
      newCasesChart {
        hasEntryForIndex(lastIndex - 1, (lastIndex - 1).f, newCaseNumber.f)
      }
    }
  }
}