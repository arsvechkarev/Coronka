package core

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers as RxSchedulers

object AndroidSchedulers : Schedulers {
  
  override fun io(): Scheduler = RxSchedulers.io()
  
  override fun computation(): Scheduler = RxSchedulers.computation()
  
  override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}