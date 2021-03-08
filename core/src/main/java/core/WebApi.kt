package core

import io.reactivex.Observable

interface WebApi {
  
  /**
   * Returns observable with string by given url
   */
  fun request(url: String): Observable<String>
  
  /**
   * Factory for creating instances of this api
   */
  interface Factory {
    
    fun create(): WebApi
  }
}