package core.di

import core.DrawerState
import core.rx.RxReceivingChannel
import core.rx.RxSendingChannel

/**
 * Module that provides channels for listening to drawer events
 */
interface DrawerStateModule {
  
  val drawerStateSendingChannel: RxSendingChannel<DrawerState>
  
  val drawerStateReceivingChannel: RxReceivingChannel<DrawerState>
}