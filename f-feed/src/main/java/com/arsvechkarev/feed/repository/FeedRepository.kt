package com.arsvechkarev.feed.repository

import com.arsvechkarev.core.model.LinkNews
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Single

class FeedRepository(
  private val firestore: FirebaseFirestore
) {
  
  fun getLatestNews(): Single<List<LinkNews>> {
    return RxFirestore.getCollection(
      firestore.collection()
    )
  }
  
}