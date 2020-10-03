package core.concurrency

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object AndroidSchedulers : core.concurrency.Schedulers {
  
  override fun io(): Scheduler = Schedulers.io()
  override fun computation(): Scheduler = Schedulers.computation()
  override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}