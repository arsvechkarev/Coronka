package coreimpl

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import core.NetworkAvailabilityNotifier
import core.NetworkListener

@SuppressLint("MissingPermission")
class AndroidNetworkAvailabilityNotifier(
  private val connectivityManager: ConnectivityManager
) : NetworkAvailabilityNotifier {
  
  private val listeners = HashMap<NetworkListener, ConnectivityManager.NetworkCallback>()
  
  override fun registerListener(listener: NetworkListener) {
    val newListener = object : ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network: Network) {
        listener.onNetworkAvailable()
      }
    }
    listeners[listener] = newListener
    connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), newListener)
  }
  
  override fun unregisterListener(listener: NetworkListener) {
    connectivityManager.unregisterNetworkCallback(listeners.getValue(listener))
  }
}