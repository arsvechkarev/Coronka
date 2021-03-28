package base.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import core.rx.RxReceivingChannel
import core.rx.StartStopRxReceiver

fun FragmentManager.transaction(block: FragmentTransaction.() -> Unit) {
  beginTransaction()
      .setReorderingAllowed(true)
      .apply(block)
      .commit()
}

fun <T> Fragment.subscribeToChannel(receiving: RxReceivingChannel<T>, onReceive: (T) -> Unit) {
  lifecycle.addObserver(StartStopRxReceiver(receiving, onReceive))
}

