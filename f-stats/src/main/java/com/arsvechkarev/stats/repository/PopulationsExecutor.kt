package com.arsvechkarev.stats.repository

import com.arsvechkarev.storage.DatabaseExecutor
import com.arsvechkarev.storage.DatabaseManager
import com.arsvechkarev.storage.dao.PopulationsDao
import com.arsvechkarev.storage.queries.PopulationsQueries
import core.Application.Threader
import core.handlers.SuccessAction
import core.handlers.SuccessHandler
import core.handlers.createSuccessHandler
import core.releasable.BaseReleasable

class PopulationsExecutor(
  private val threader: Threader,
  private val populationsDao: PopulationsDao
) : BaseReleasable() {
  
  private var populationHandler: SuccessHandler<Int>? = null
  
  init {
   addForRelease(populationHandler)
  }
  
  fun getPopulationByIso2(iso2: String, action: SuccessAction<Int>) {
    if (populationHandler == null) populationHandler = createSuccessHandler(action)
    populationHandler!!.runIfNotAlready {
      threader.ioWorker.submit {
        DatabaseManager.instance.readableDatabase.use {
          val cursor = DatabaseExecutor.executeQuery(it, PopulationsQueries.populationQuery(iso2))
          threader.mainThreadWorker.submit {
            populationHandler?.dispatchSuccess(populationsDao.getPopulation(cursor))
          }
        }
      }
    }
  }
}