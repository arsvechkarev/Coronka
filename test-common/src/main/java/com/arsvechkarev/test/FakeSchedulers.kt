package com.arsvechkarev.test

import core.rx.Schedulers
import io.reactivex.schedulers.Schedulers as RxSchedulers

object FakeSchedulers : Schedulers {
  
  override fun io() = RxSchedulers.trampoline()
  
  override fun computation() = RxSchedulers.trampoline()
  
  override fun mainThread() = RxSchedulers.trampoline()
}