package core.auth

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import io.reactivex.Completable
import io.reactivex.Single

interface Authenticator {
  
  fun isUserLoggedIn(): Boolean
  
  fun sendSignInLinkToEmail(email: String, settings: ActionCodeSettings): Completable
  
  fun isSignInWithEmailLink(email: String): Boolean
  
  fun signInWithEmailLink(email: String, emailLink: String): Single<AuthResult>
}