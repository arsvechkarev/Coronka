package core

import io.reactivex.Scheduler

/**
 * Scheduler for RxJava
 */
interface Schedulers {
  
  fun io(): Scheduler
  
  fun computation(): Scheduler
  
  fun mainThread(): Scheduler
}