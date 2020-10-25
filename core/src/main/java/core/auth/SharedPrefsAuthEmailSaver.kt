package core.auth

import android.content.Context

class SharedPrefsAuthEmailSaver(context: Context) : AuthEmailSaver {
  
  private val sharedPrefs = context.getSharedPreferences(EMAIL_SAVER_FILENAME, Context.MODE_PRIVATE)
  
  override fun saveEmail(email: String) {
    sharedPrefs.edit().putString(EMAIL_SAVER_KEY, email).apply()
  }
  
  override fun getEmail(): String? {
    return sharedPrefs.getString(EMAIL_SAVER_KEY, null)
  }
  
  companion object {
    
    const val EMAIL_SAVER_FILENAME = "RegistrationFile"
    const val EMAIL_SAVER_KEY = "RegistrationEmailKey"
  }
}