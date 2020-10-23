package core.auth

import com.google.firebase.auth.ActionCodeSettings

val AuthSettings: ActionCodeSettings
  get() = ActionCodeSettings.newBuilder()
      .setUrl("https://coronka.com/auth")
      .setHandleCodeInApp(true)
      .setAndroidPackageName("com.arsvechkarev.coronka", true, "1")
      .build()