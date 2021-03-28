package coreimpl

import core.DrawerState
import core.di.DrawerStateModule
import core.rx.RxSubjectChannel
import io.reactivex.subjects.BehaviorSubject

object DefaultDrawerStateModule : DrawerStateModule {
  
  private val rxDrawerStateChannel = RxSubjectChannel(BehaviorSubject.create<DrawerState>())
  
  override val drawerStateSendingChannel = rxDrawerStateChannel
  
  override val drawerStateReceivingChannel = rxDrawerStateChannel
}