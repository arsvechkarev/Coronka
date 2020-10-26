package core.auth

import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import core.extenstions.asCompletable
import core.extenstions.asObservable

object FirebaseAuthenticator : Authenticator {
  
  override fun isUserLoggedIn(): Boolean {
    return FirebaseAuth.getInstance().currentUser != null
  }
  
  override fun sendSignInLinkToEmail(
    email: String,
    settings: ActionCodeSettings
  ) = FirebaseAuth.getInstance().sendSignInLinkToEmail(email, settings).asCompletable()
  
  override fun isSignInWithEmailLink(email: String): Boolean {
    return FirebaseAuth.getInstance().isSignInWithEmailLink(email)
  }
  
  override fun signInWithEmailLink(
    email: String,
    emailLink: String
  ) = FirebaseAuth.getInstance().signInWithEmailLink(email, emailLink).asObservable()
}