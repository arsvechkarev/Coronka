package core

import io.reactivex.Observable

interface Networker {
  
  /**
   * Returns observable with string by given url
   */
  fun request(url: String): Observable<String>
}