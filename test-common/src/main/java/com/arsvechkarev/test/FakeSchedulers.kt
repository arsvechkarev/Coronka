package com.arsvechkarev.test

import core.Schedulers
import io.reactivex.schedulers.Schedulers as RxSchedulers

object FakeSchedulers : Schedulers {
  
  override fun io() = RxSchedulers.trampoline()
  
  override fun computation() = RxSchedulers.trampoline()
  
  override fun mainThread() = RxSchedulers.trampoline()
}