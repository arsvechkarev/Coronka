package core

import io.reactivex.Scheduler

/**
 * Schedulers for RxJava
 */
interface Schedulers {
  
  fun io(): Scheduler
  
  fun computation(): Scheduler
  
  fun mainThread(): Scheduler
}