package core

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.ConnectivityManager.OnNetworkActiveListener

@SuppressLint("MissingPermission")
class AndroidNetworkAvailabilityNotifier(
  private val connectivityManager: ConnectivityManager
) : NetworkAvailabilityNotifier {
  
  private val listeners = HashMap<NetworkListener, OnNetworkActiveListener>()
  
  override fun registerListener(listener: NetworkListener) {
    val newListener = OnNetworkActiveListener { listener.onNetworkAvailable() }
    listeners[listener] = newListener
    connectivityManager.addDefaultNetworkActiveListener(newListener)
  }
  
  override fun unregisterListener(listener: NetworkListener) {
    connectivityManager.removeDefaultNetworkActiveListener(listeners.getValue(listener))
  }
}