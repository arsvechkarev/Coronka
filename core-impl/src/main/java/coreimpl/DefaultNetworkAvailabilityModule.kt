package coreimpl

import android.content.Context
import android.net.ConnectivityManager
import core.di.NetworkAvailabilityModule
import core.rx.RxReceivingChannel
import core.rx.RxSendingChannel
import core.rx.RxSubjectChannel
import io.reactivex.subjects.PublishSubject

class DefaultNetworkAvailabilityModule(applicationContext: Context) : NetworkAvailabilityModule {
  
  private val channel = RxSubjectChannel<Unit>(PublishSubject.create())
  
  override val networkAvailabilitySendingChannel: RxSendingChannel<Unit> = channel
  
  override val networkAvailabilityChannel: RxReceivingChannel<Unit> = channel
  
  override val networkAvailabilityNotifier = AndroidNetworkAvailabilityNotifier(
    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
}