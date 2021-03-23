package coreimpl

import android.content.Context
import com.arsvechkarev.core.R
import core.FailureReason
import core.FailureReasonToMessageConverter
import core.di.FailureReasonToMessageModule

class FailureReasonToMessageModuleImpl(private val context: Context) :
  FailureReasonToMessageModule {
  
  override val failureReasonToMessageConverter = object : FailureReasonToMessageConverter {
    
    override fun getMessageForReason(reason: FailureReason) = when (reason) {
      FailureReason.NO_CONNECTION -> context.getString(R.string.error_no_connection)
      FailureReason.TIMEOUT -> context.getString(R.string.error_timeout)
      FailureReason.UNKNOWN -> context.getString(R.string.error_unknown)
    }
  }
}